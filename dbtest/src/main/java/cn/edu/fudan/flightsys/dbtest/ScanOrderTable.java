package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.Flight;
import cn.edu.fudan.flightsys.pojo.Ticket;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by junfeng on 12/10/15.
 */
public class ScanOrderTable {
    public static final String order_cf_o_cq = "startplace:arrivalplace:starttime:arrivaltime:price:isfirst";

    public static Map<String, Object> orderCQToMap(byte[] cq) {
        int n = cq.length;
        String startEnd = Bytes.toString(cq, 0, n - 13);
        String[] places = startEnd.split(":", 2);
        short startTime = Bytes.toShort(cq, n - 13, 2);
        short arrivalTime = Bytes.toShort(cq, n - 11, 2);
        double price = Bytes.toDouble(cq, n - 9);
        boolean isFirst = cq[n - 1] != (byte) 0;
        Map<String, Object> cqM = new HashMap<>();
        cqM.put("startplace", places[0]);
        cqM.put("arrivalplace", places[1]);
        cqM.put("starttime", Flight.getPrintableTime(startTime));
        cqM.put("arrivaltime", Flight.getPrintableTime(arrivalTime));
        cqM.put("price", price);
        cqM.put("isfirst", isFirst);
        return cqM;
    }

    public static void main(String[] args) throws IOException {
        Connection connection = ConnectionFactory.createConnection();
        Table orderTable = connection.getTable(
                TableName.valueOf(HBaseConstants.FLIGHT_NAMESPACE,
                        HBaseConstants.ORDER_TABLE_NAME)
        );
        Scan scan = new Scan();
        scan.setBatch(0);
        scan.setCaching(10);
        scan.addFamily(Bytes.toBytes("o"));
        ResultScanner resultScanner = orderTable.getScanner(scan);
        int limit = 0;
        for (Result res : resultScanner) {
            byte[] rowKey = res.getRow();
            System.out.format("rowKey=%s:%s:%d%n",
                    Bytes.toString(rowKey, 0, rowKey.length - 8),
                    Ticket.getPrintableDate(Bytes.toInt(rowKey, rowKey.length - 8, 4)),
                    Bytes.toInt(rowKey, rowKey.length - 4, 4));
            Map<byte[], byte[]> orderMap = res.getFamilyMap(Bytes.toBytes("o"));
            for (byte[] cq : orderMap.keySet()) {
                Map<String, Object> cqM = orderCQToMap(cq);
                System.out.format("%s:%s-%s-%s-%s-%s%n",
                        cqM.get("startplace"),
                        cqM.get("arrivalplace"),
                        cqM.get("starttime"),
                        cqM.get("arrivaltime"),
                        cqM.get("price"),
                        cqM.get("isfirst"));
                System.out.println("paid status: " + Bytes.toBoolean(orderMap.get(cq)));
            }
            limit++;
            if (limit >= 10) break;
        }
    }
}
