package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.Flight;
import cn.edu.fudan.flightsys.pojo.Order;
import cn.edu.fudan.flightsys.pojo.Ticket;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by junfeng on 12/8/15.
 */
public class HBaseDB extends DB {

    private Connection connection;
    private Table userTable;
    private Table flightTable;
    private Table orderTable;

    @Override
    public void init() throws IOException {
        super.init();
        connection = ConnectionFactory.createConnection();
        userTable = connection.getTable(
                TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE,
                        HBaseConstants.USER_TABLE_NAME)
        );
        flightTable = connection.getTable(
                TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE,
                        HBaseConstants.FLIGHT_TABLE_NAME)
        );
        orderTable = connection.getTable(
                TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE,
                        HBaseConstants.ORDER_TABLE_NAME)
        );
    }

    @Override
    public void cleanup() throws IOException {
        super.cleanup();
        connection.close();
    }

    @Override
    public boolean login(String username, String password) {
        Get get = new Get(Bytes.toBytes(username));
        get.addFamily(HBaseUserTableImporter.USER_INFO_CF);
        try {
            Result result = userTable.get(get);
            // result.getFamilyMap(HBaseUserTableImporter.USER_INFO_CF);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Ticket> searchTicket(String startPlace, String endPlace, LocalDate startDate, LocalDate endDate) {
        Scan scan = new Scan();
        scan.setBatch(0);
        scan.setCaching(10);
        scan.addFamily(HBaseFlightTableTicketCFImporter.TICKET_INFO_CF);
        String startEnd = startPlace + ":" + endPlace;

        byte[] rowPrefix = Bytes.toBytes(startEnd);
        scan.setStartRow(Bytes.add(rowPrefix,
                Bytes.toBytes(Ticket.getIntValueFromLocalDate(startDate)),
                Bytes.toBytes(0))
        );
        scan.setStopRow(Bytes.add(rowPrefix,
                Bytes.toBytes(Ticket.getIntValueFromLocalDate(endDate)),
                Bytes.toBytes(Integer.MAX_VALUE)));
        ResultScanner resultScanner = null;
        try {
            resultScanner = flightTable.getScanner(scan);
            List<Ticket> tickets = new ArrayList<>();
            for (Result res : resultScanner) {
                tickets.add(HBaseOrderTableImporter.constructTicketFromResult(res));
            }
            resultScanner.close();
            return tickets;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean orderPayTicket(String username, Ticket ticket, boolean isFirst) {
        Flight flight = ticket.getFlight();
        byte[] rowKey = HBaseOrderTableImporter.constructFlightRowKey(
                flight.getStartPlace(), flight.getArrivalPlace(),
                ticket.getFlightDate(), flight.getId());
        long cur = -1;
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cur > 0) {
            byte[] orderRowKey = HBaseOrderTableImporter.constructOrderRowKey(
                    username, ticket.getFlightDate(), flight.getId());
            Put put = new Put(orderRowKey);
            // already paid
            put.addColumn(HBaseOrderTableImporter.ORDER_INFO_CF,
                    HBaseOrderTableImporter.constructOrderCQ(ticket, isFirst),
                    Bytes.toBytes(true));
            try {
                orderTable.put(put);
                int point;
                if (isFirst) point = 1;
                else point = 2;
                userTable.incrementColumnValue(
                        Bytes.toBytes(username),
                        HBaseUserTableImporter.USER_INFO_CF,
                        HBaseUserTableImporter.USER_POINT_CQ,
                        point);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void searchHistory(String username, LocalDate startDate, LocalDate endDate) {
        Scan scan = new Scan();
        byte[] rowPrefix = Bytes.toBytes(username);
        scan.setStartRow(Bytes.add(rowPrefix,
                Bytes.toBytes(Ticket.getIntValueFromLocalDate(startDate)),
                Bytes.toBytes(0))
        );
        scan.setStopRow(Bytes.add(rowPrefix,
                Bytes.toBytes(Ticket.getIntValueFromLocalDate(endDate)),
                Bytes.toBytes(Integer.MAX_VALUE)));
        scan.setBatch(0);
        scan.setCaching(100);
        scan.addFamily(Bytes.toBytes("o"));
        try (ResultScanner resultScanner = orderTable.getScanner(scan)){
            for (Result res : resultScanner) {
                byte[] rowKey = res.getRow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
