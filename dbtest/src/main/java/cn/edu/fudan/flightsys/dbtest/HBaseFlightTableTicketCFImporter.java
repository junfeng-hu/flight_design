package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.Airplane;
import cn.edu.fudan.flightsys.pojo.Flight;
import cn.edu.fudan.flightsys.pojo.Ticket;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by junfeng on 12/7/15.
 */
public class HBaseFlightTableTicketCFImporter {
    public static final byte[] TICKET_INFO_CF = Bytes.toBytes("t");
    public static final byte[] TICKET_FIRST_COUNT_CQ = Bytes.toBytes("firstcount");
    public static final byte[] TICKET_ECONOMY_COUNT_CQ = Bytes.toBytes("economycount");
    public static final byte[] TICKET_FIRST_PRICE_CQ = Bytes.toBytes("firstprice");
    public static final byte[] TICKET_ECONOMY_PRICE_CQ = Bytes.toBytes("economyprice");
    public static final byte[] TICKET_START_TIME_CQ = Bytes.toBytes("starttime");
    public static final byte[] TICKET_ARRIVAL_TIME_CQ = Bytes.toBytes("arrivaltime");

    Configuration conf = HBaseConfiguration.create();
    LocalDate startDate;
    int days;

    HBaseFlightTableTicketCFImporter(LocalDate startDate, int days) {
        this.startDate = startDate;
        this.days = days;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("usage:%n startDate(%Y-%m-%d) days(from startDate)");
            System.exit(1);
        }
        LocalDate startDate = LocalDate.parse(args[0]);
        int days = Integer.valueOf(args[1]);
        HBaseFlightTableTicketCFImporter flightTableTicketCFImporter = new HBaseFlightTableTicketCFImporter(startDate, days);

        System.out.println("starting import flights to HBase ...");
        flightTableTicketCFImporter.putTicketsToHBase();
        System.out.println("end import flights to HBase ...");
    }

    public Flight constructFlightFromResult(Result res) {
        byte[] rowKey = res.getRow();
        String startEnd = Bytes.toString(rowKey, 0, rowKey.length - 8);
        String[] places = startEnd.split(":", 2);
        Flight flight = new Flight();
        flight.setStartPlace(places[0]);
        flight.setArrivalPlace(places[1]);
        flight.setId(Bytes.toInt(rowKey, rowKey.length - 4, 4));
        Map<byte[], byte[]> flightMap = res.getFamilyMap(HBaseFlightTableImporter.FLIGHT_INFO_CF);
        flight.setStartTime(Bytes.toShort(flightMap.get(HBaseFlightTableImporter.FLIGHT_START_TIME_CQ)));
        flight.setArrivalTime(Bytes.toShort(flightMap.get(HBaseFlightTableImporter.FLIGHT_ARRIVAL_TIME_CQ)));
        Airplane airplane = new Airplane();
        airplane.setId(Bytes.toInt(flightMap.get(HBaseFlightTableImporter.FLIGHT_ID_AIRPLANE_CQ)));
        airplane.setFirstClass(Bytes.toLong(flightMap.get(HBaseFlightTableImporter.FLIGHT_FIRST_CLASS_CQ)));
        airplane.setEconomyClass(Bytes.toLong(flightMap.get(HBaseFlightTableImporter.FLIGHT_ECONOMY_CLASS_CQ)));
        flight.setAirplane(airplane);
        return flight;
    }

    public double sampleFirstPrice() {
        return Math.random() * (3000 - 2000) + 2000;
    }

    public double sampleEconomyPrice() {
        return Math.random() * (1500 - 1000) + 1000;
    }

    public Put constructPutFromFlight(Flight flight, LocalDate flightDate, Table flightTable) throws IOException {
        // 1000-1500, 2000-3000
        String startEnd = flight.getStartPlace() + ":" + flight.getArrivalPlace();
        byte[] rowKey = Bytes.add(
                Bytes.toBytes(startEnd),
                Bytes.toBytes(Ticket.getIntValueFromLocalDate(flightDate)),
                Bytes.toBytes(flight.getId())
        );
        Put put = new Put(rowKey);
        flightTable.incrementColumnValue(rowKey, TICKET_INFO_CF,
                TICKET_FIRST_COUNT_CQ, flight.getAirplane().getFirstClass());
        flightTable.incrementColumnValue(rowKey, TICKET_INFO_CF,
                TICKET_ECONOMY_COUNT_CQ, flight.getAirplane().getEconomyClass());
        put.addColumn(TICKET_INFO_CF, TICKET_FIRST_PRICE_CQ, Bytes.toBytes(sampleFirstPrice()));
        put.addColumn(TICKET_INFO_CF, TICKET_ECONOMY_PRICE_CQ, Bytes.toBytes(sampleEconomyPrice()));
        put.addColumn(TICKET_INFO_CF, TICKET_START_TIME_CQ, Bytes.toBytes(flight.getStartTime()));
        put.addColumn(TICKET_INFO_CF, TICKET_ARRIVAL_TIME_CQ, Bytes.toBytes(flight.getArrivalTime()));
        return put;

    }

    public List<Flight> getFlightsFromHBase() throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName flightTableName = TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE, HBaseConstants.FLIGHT_TABLE_NAME);
        Table flightTable = connection.getTable(flightTableName);
        Scan scan = new Scan();
        scan.addFamily(HBaseFlightTableImporter.FLIGHT_INFO_CF);
        scan.setBatch(0);
        scan.setCaching(1000);
        ResultScanner resultScanner = flightTable.getScanner(scan);
        List<Flight> flights = new ArrayList<>();
        for (Result res : resultScanner) {
            Flight flight = constructFlightFromResult(res);
            // System.out.println(flight);
            flights.add(flight);
        }
        resultScanner.close();
        return flights;
    }

    public void putTicketsToHBase() throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName tableName = TableName.valueOf(
                HBaseConstants.FLIGHT_NAMESPACE,
                HBaseConstants.FLIGHT_TABLE_NAME);
        Table table = connection.getTable(tableName);
        List<Flight> flights = getFlightsFromHBase();
        List<Put> puts = new ArrayList<>(days * flights.size());
        for (Flight flight : flights) {
            for (int i = 0; i < days; ++i) {
                LocalDate cur = startDate.plusDays(i);
                Put put = constructPutFromFlight(flight, cur, table);
                puts.add(put);
            }
            if (puts.size() >= 10000) {
                table.put(puts);
                System.out.println("puts " + puts.size() + " tickets to HBase");
                System.out.println("current flight: " + flight);
                puts.clear();
            }
        }
        System.out.println("puts " + puts.size() + " tickets to HBase");
        table.put(puts);
    }
}
