package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.City;
import cn.edu.fudan.flightsys.pojo.Flight;
import cn.edu.fudan.flightsys.pojo.Ticket;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by junfeng on 12/5/15.
 */
public class HBaseOrderTableImporter {
    public static final byte[] ORDER_INFO_CF = Bytes.toBytes("o");
    Connection connection;
    private LocalDate startDate;
    private int days;
    private Random random = new Random();

    public HBaseOrderTableImporter(LocalDate startDate, int days) throws IOException {
        this.startDate = startDate;
        this.days = days;
        connection = ConnectionFactory.createConnection();
    }

    public static int[][] constructToIndexMap(int n) {
        int[][] toIndex = new int[n][n - 1];
        for (int i = 0; i < n; ++i) {
            int count = 0;
            for (int j = 0; j < n; ++j) {
                if (i != j) {
                    toIndex[i][count++] = j;
                }
            }
        }
        return toIndex;
    }

    public static byte[] constructFlightRowKeyPrefix(
            String startCity,
            String endCity,
            LocalDate flightDate) {
        String startEnd = startCity + ":" + endCity;
        return Bytes.add(Bytes.toBytes(startEnd),
                Bytes.toBytes(Ticket.getIntValueFromLocalDate(flightDate))
        );
    }

    public static byte[] constructFlightRowKey(
            String startCity,
            String endCity,
            int flightDate,
            int flightId) {
        String startEnd = startCity + ":" + endCity;
        return Bytes.add(Bytes.toBytes(startEnd),
                Bytes.toBytes(flightDate),
                Bytes.toBytes(flightId)
        );
    }

    public static byte[] constructOrderRowKey(String username, int flightDate, int flightId) {
        return Bytes.add(
                Bytes.toBytes(username),
                Bytes.toBytes(flightDate),
                Bytes.toBytes(flightId)
        );
    }

    public static byte[] constructOrderCQ(Ticket ticket, boolean isFirst) {
        // String order_cf_o_cq = "startplace:arrivalplace:starttime:arrivaltime:price:isfirst";
        Flight flight = ticket.getFlight();
        String startEnd = flight.getStartPlace() + ":" + flight.getArrivalPlace();
        byte[] head = Bytes.add(
                Bytes.toBytes(startEnd),
                Bytes.toBytes(flight.getStartTime()),
                Bytes.toBytes(flight.getArrivalTime())
        );
        byte[] tail;
        if (isFirst) {
            tail = Bytes.add(Bytes.toBytes(ticket.getFirstPrice()), Bytes.toBytes(true));
        } else {
            tail = Bytes.add(Bytes.toBytes(ticket.getEconomyPrice()), Bytes.toBytes(false));
        }
        return Bytes.add(head, tail);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("usage:%n startDate(%Y-%m-%d) days(from startDate)");
            System.exit(1);
        }
        LocalDate startDate = LocalDate.parse(args[0]);
        int days = Integer.valueOf(args[1]);
        HBaseOrderTableImporter orderTableImporter = new HBaseOrderTableImporter(startDate, days);

        System.out.println("starting import orders to HBase ...");
        orderTableImporter.putOrdersToHBase();
        System.out.println("end import orders to HBase ...");
    }

    public static Ticket constructTicketFromResult(Result res) {
        byte[] rowKey = res.getRow();
        String startEnd = Bytes.toString(rowKey, 0, rowKey.length - 8);
        String[] places = startEnd.split(":", 2);
        Ticket ticket = new Ticket();
        ticket.setFlightDate(Bytes.toInt(rowKey, rowKey.length - 8, 4));

        Flight flight = new Flight();
        flight.setStartPlace(places[0]);
        flight.setArrivalPlace(places[1]);
        flight.setId(Bytes.toInt(rowKey, rowKey.length - 4, 4));
        Map<byte[], byte[]> ticketMap = res.getFamilyMap(HBaseFlightTableTicketCFImporter.TICKET_INFO_CF);
        flight.setStartTime(Bytes.toShort(ticketMap.get(HBaseFlightTableTicketCFImporter.TICKET_START_TIME_CQ)));
        flight.setArrivalTime(Bytes.toShort(ticketMap.get(HBaseFlightTableTicketCFImporter.TICKET_ARRIVAL_TIME_CQ)));
        ticket.setFlight(flight);

        ticket.setFirstCount(Bytes.toInt(ticketMap.get(HBaseFlightTableTicketCFImporter.TICKET_FIRST_COUNT_CQ)));
        ticket.setFirstPrice(Bytes.toDouble(ticketMap.get(HBaseFlightTableTicketCFImporter.TICKET_FIRST_PRICE_CQ)));
        ticket.setEconomyCount(Bytes.toInt(ticketMap.get(HBaseFlightTableTicketCFImporter.TICKET_ECONOMY_COUNT_CQ)));
        ticket.setEconomyPrice(Bytes.toDouble(ticketMap.get(HBaseFlightTableTicketCFImporter.TICKET_ECONOMY_PRICE_CQ)));
        return ticket;
    }

    private List<Ticket> searchTicket(City start, City end, LocalDate flightDate) throws IOException {
        Table flightTable = connection.getTable(
                TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE,
                        HBaseConstants.FLIGHT_TABLE_NAME));
        Scan scan = new Scan();
        scan.addFamily(HBaseFlightTableTicketCFImporter.TICKET_INFO_CF);
        byte[] rowPrefix = constructFlightRowKeyPrefix(start.getCityName(),
                end.getCityName(), flightDate);
        scan.setStartRow(Bytes.add(rowPrefix, Bytes.toBytes(0)));
        scan.setStopRow(Bytes.add(rowPrefix, Bytes.toBytes(Integer.MAX_VALUE)));
        ResultScanner resultScanner = flightTable.getScanner(scan);
        List<Ticket> tickets = new ArrayList<>();
        for (Result res : resultScanner) {
            tickets.add(constructTicketFromResult(res));
        }
        resultScanner.close();
        return tickets;
    }

    private void payOrderTicket(String username, Ticket ticket, boolean isFirst) throws IOException {
        Table orderTable = connection.getTable(
                TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE,
                        HBaseConstants.ORDER_TABLE_NAME));
        Flight flight = ticket.getFlight();
        byte[] rowKey = constructOrderRowKey(username, ticket.getFlightDate(), flight.getId());
        Put put = new Put(rowKey);
        // already paid
        put.addColumn(ORDER_INFO_CF, constructOrderCQ(ticket, isFirst), Bytes.toBytes(true));
        orderTable.put(put);
    }

    private long updateUserPoint(String username, boolean isFirst) throws IOException {
        int point;
        if (isFirst) point = 1;
        else point = 2;
        Table userTable = connection.getTable(
                TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE, HBaseConstants.USER_TABLE_NAME));
        return userTable.incrementColumnValue(
                Bytes.toBytes(username),
                HBaseUserTableImporter.USER_INFO_CF,
                HBaseUserTableImporter.USER_POINT_CQ,
                point);
    }

    public long orderTicket(String username, Ticket ticket, boolean isFirst) throws IOException {
        Table flightTable = connection.getTable(
                TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE,
                        HBaseConstants.FLIGHT_TABLE_NAME));
        Flight flight = ticket.getFlight();
        byte[] rowKey = constructFlightRowKey(flight.getStartPlace(), flight.getArrivalPlace(),
                ticket.getFlightDate(), flight.getId());
        long cur;
        if (isFirst) {
            cur = flightTable.incrementColumnValue(rowKey,
                    HBaseFlightTableTicketCFImporter.TICKET_INFO_CF,
                    HBaseFlightTableTicketCFImporter.TICKET_FIRST_COUNT_CQ,
                    -1L
            );
        } else {
            cur = flightTable.incrementColumnValue(rowKey,
                    HBaseFlightTableTicketCFImporter.TICKET_INFO_CF,
                    HBaseFlightTableTicketCFImporter.TICKET_ECONOMY_COUNT_CQ,
                    -1L
            );
        }

        return cur;
    }

    public void putOrdersToHBase() throws IOException {
        List<City> cityList = HBaseFlightTableImporter.readCitiesFromCsv();
        int n = cityList.size();
        City[] cities = cityList.toArray(new City[n]);
        int[][] toIndex = constructToIndexMap(n);
        List<String> users = HBaseUserTableImporter.readUsersFromCsv();

        for (String username : users) {
            int startCityIndex = random.nextInt(n);
            City start = cities[startCityIndex];
            System.out.println(username + " start in " + start.getCityName());
            for (int i = 0; i < days; ++i) {
                int nextCityIndex = toIndex[startCityIndex][random.nextInt(n - 1)];
                City next = cities[nextCityIndex];
                LocalDate flightDate = startDate.plusDays(i);
                List<Ticket> curDayTickets = searchTicket(start, next, flightDate);
                Ticket hitTicket = curDayTickets.get(random.nextInt(curDayTickets.size()));
                boolean isFirst = random.nextBoolean();
                long cur = orderTicket(username, hitTicket, isFirst);
                // check cur >= 0
                if (cur >= 0) {
                    // success
                    // System.out.println(hitTicket);
                    payOrderTicket(username, hitTicket, isFirst);
                    updateUserPoint(username, isFirst);
                }
                // leave
                startCityIndex = nextCityIndex;
                start = cities[startCityIndex];
            }
            System.out.println(username + " end in " + start.getCityName());
        }
    }
}
