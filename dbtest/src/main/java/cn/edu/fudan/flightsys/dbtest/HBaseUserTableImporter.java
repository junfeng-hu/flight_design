package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.User;
import com.google.protobuf.ServiceException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by junfeng on 12/5/15.
 */
public class HBaseUserTableImporter {
    public static final byte[] USER_INFO_CF = Bytes.toBytes("i");
    public static final byte[] USER_EMAIL_CQ = Bytes.toBytes("email");
    public static final byte[] USER_PASSWORD_CQ = Bytes.toBytes("password");
    public static final byte[] USER_CREATE_TIME_CQ = Bytes.toBytes("create_time");
    public static final byte[] USER_POINT_CQ = Bytes.toBytes("point");
    Configuration conf = HBaseConfiguration.create();

    public static List<String> readUsersFromCsv() throws IOException {
        FileReader reader = new FileReader(HBaseConstants.USERS_FILE);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
        List<String> names = new ArrayList<>();
        for (CSVRecord record : records) {
            names.add(record.get(0));
        }
        return names;
    }

    public static void main(String[] args) throws IOException, ServiceException {
        printHBaseCluster();
        HBaseUserTableImporter userTableImporter = new HBaseUserTableImporter();
        System.out.println("starting import users to HBase ...");
        userTableImporter.putUsersToHBase();
        System.out.println("end import users to HBase ...");
    }

    public static void printHBaseCluster() throws IOException, ServiceException {
        Configuration conf = HBaseConfiguration.create();
        HBaseAdmin.checkHBaseAvailable(conf);
        Connection connection = ConnectionFactory.createConnection(conf);
        Admin admin = connection.getAdmin();
        System.out.println(admin.getClusterStatus());
        TableName[] tableNames = admin.listTableNamesByNamespace(HBaseConstants.FLIGHT_NAMESPACE);
        for (TableName tableName : tableNames) {
            System.out.println(tableName);
        }
    }

    public User constructUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setCreate_time(System.currentTimeMillis());
        user.setEmail(username + HBaseConstants.EMAIL_SUFFIX);
        user.setPassword(username);
        user.setPoint(0L);
        return user;
    }

    public Put constructPutFromUser(User user, Table userTable) throws IOException {
        byte[] rowKey = Bytes.toBytes(user.getUsername());
        Put put = new Put(rowKey);
        put.addColumn(USER_INFO_CF, USER_EMAIL_CQ, Bytes.toBytes(user.getEmail()));
        put.addColumn(USER_INFO_CF, USER_PASSWORD_CQ, Bytes.toBytes(user.getPassword()));
        put.addColumn(USER_INFO_CF, USER_CREATE_TIME_CQ, Bytes.toBytes(user.getCreate_time()));
        userTable.incrementColumnValue(rowKey, USER_INFO_CF,
                USER_POINT_CQ, user.getPoint());
        return put;
    }

    public void putUsersToHBase() throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName tableName = TableName.valueOf(
                HBaseConstants.FLIGHT_NAMESPACE,
                HBaseConstants.USER_TABLE_NAME);
        Table table = connection.getTable(tableName);
        List<Put> puts = new ArrayList<>();
        for (String username : readUsersFromCsv()) {
            User user = constructUser(username);
            Put put = constructPutFromUser(user, table);
            puts.add(put);
            if (puts.size() >= 1000) {
                table.put(puts);
                System.out.println("puts " + puts.size() + " users to HBase");
                puts.clear();
            }
        }
        System.out.println("puts " + puts.size() + " users to HBase");
        table.put(puts);
    }
}
