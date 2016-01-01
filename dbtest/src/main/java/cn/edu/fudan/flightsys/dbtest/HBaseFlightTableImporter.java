package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.Airplane;
import cn.edu.fudan.flightsys.pojo.City;
import cn.edu.fudan.flightsys.pojo.Flight;
import com.google.protobuf.ServiceException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by junfeng on 12/5/15.
 */
public class HBaseFlightTableImporter {
    public static final byte[] FLIGHT_INFO_CF = Bytes.toBytes("a");
    public static final byte[] FLIGHT_FIRST_CLASS_CQ = Bytes.toBytes("firstclass");
    public static final byte[] FLIGHT_ECONOMY_CLASS_CQ = Bytes.toBytes("economyclass");
    public static final byte[] FLIGHT_ID_AIRPLANE_CQ = Bytes.toBytes("idairplane");
    public static final byte[] FLIGHT_START_TIME_CQ = Bytes.toBytes("starttime");
    public static final byte[] FLIGHT_ARRIVAL_TIME_CQ = Bytes.toBytes("arrivaltime");
    public static final long maxSeats = 77;
    Configuration conf = HBaseConfiguration.create();
    private Random random = new Random();

    public static List<City> readCitiesFromCsv() throws IOException {
        FileReader reader = new FileReader(HBaseConstants.CITIES_FILE);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HBaseConstants.CITIES_FILE_HEADER)
                .withSkipHeaderRecord().parse(reader);
        List<City> cities = new ArrayList<>();
        for (CSVRecord record : records) {
            if (Long.valueOf(record.get("level")) == 2) {
                // city
                City city = new City();
                city.setCityName(record.get("areaname"));
                city.setLng(Double.valueOf(record.get("lng")));
                city.setLat(Double.valueOf(record.get("lat")));
                cities.add(city);
            }
        }
        return cities;
    }

    public static void main(String[] args) throws IOException, ServiceException {
        HBaseFlightTableImporter flightTableImporter = new HBaseFlightTableImporter();
        System.out.println("starting import flights to HBase ...");
        flightTableImporter.putFlightsToHBase();
        System.out.println("end import flights to HBase ...");
    }

    public short sampleStartTime() {
        int hour = random.nextInt(20);
        return (short) (hour << 8);
    }

    public long sampleFirstSeats() {
        return (long) (Math.random() * (30 - 20) + 20);
    }
    /*
    public double maxHowLong = 0;
    */

    public Airplane constructAirplane(int airplaneId) {
        Airplane airplane = new Airplane();
        airplane.setId(airplaneId);
        long firstClass = sampleFirstSeats();
        airplane.setFirstClass(firstClass);
        airplane.setEconomyClass(maxSeats - firstClass);
        return airplane;
    }

    public Flight constructFlight(int flightId, Airplane airplane, City start, City end) {
        Flight flight = new Flight();
        flight.setAirplane(airplane);
        flight.setId(flightId);
        flight.setStartPlace(start.getCityName());
        flight.setArrivalPlace(end.getCityName());
        short startTime = sampleStartTime();
        /**
         double howLong = Math.sqrt(Math.pow(start.getLat() - end.getLat(), 2)
         + Math.pow(start.getLng() - end.getLng(), 2));
         if (maxHowLong < howLong) {
         maxHowLong = howLong;
         }
         */
        flight.setStartTime(startTime);

        // all flights fly 2 hours
        short arrivalTime = (short) (startTime + (short) (2 << 8));
        flight.setArrivalTime(arrivalTime);
        // System.out.println("startTime: " + Flight.getPrintableTime(startTime));
        // System.out.println("arrivalTime: " + Flight.getPrintableTime(arrivalTime));
        return flight;
    }

    public Put constructPutFromFlight(Flight flight) {
        String startEnd = flight.getStartPlace() + ":" + flight.getArrivalPlace();

        Bytes.toBytes(startEnd);
        Put put = new Put(
                Bytes.add(
                        Bytes.toBytes(startEnd),
                        Bytes.toBytes(HBaseConstants.MAX_FLIGHT_DATE),
                        Bytes.toBytes(flight.getId())
                )
        );
        put.addColumn(FLIGHT_INFO_CF, FLIGHT_FIRST_CLASS_CQ, Bytes.toBytes(flight.getAirplane().getFirstClass()));
        put.addColumn(FLIGHT_INFO_CF, FLIGHT_ECONOMY_CLASS_CQ, Bytes.toBytes(flight.getAirplane().getEconomyClass()));
        put.addColumn(FLIGHT_INFO_CF, FLIGHT_ID_AIRPLANE_CQ, Bytes.toBytes(flight.getAirplane().getId()));
        put.addColumn(FLIGHT_INFO_CF, FLIGHT_START_TIME_CQ, Bytes.toBytes(flight.getStartTime()));
        put.addColumn(FLIGHT_INFO_CF, FLIGHT_ARRIVAL_TIME_CQ, Bytes.toBytes(flight.getArrivalTime()));
        return put;
    }

    public void putFlightsToHBase() throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName tableName = TableName.valueOf(
                HBaseConstants.FLIGHT_NAMESPACE,
                HBaseConstants.FLIGHT_TABLE_NAME);
        Table table = connection.getTable(tableName);
        List<Put> puts = new ArrayList<>();
        List<City> cityList = readCitiesFromCsv();
        int n = cityList.size();
        City[] cities = cityList.toArray(new City[n]);
        int flightId = 1;
        int airplaneId = 1;
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                City start = cities[i];
                City end = cities[j];
                Airplane airplane = constructAirplane(airplaneId);
                Flight flight = constructFlight(
                        flightId, airplane, start, end
                );
                puts.add(constructPutFromFlight(flight));
                Flight backFlight = constructFlight(
                        flightId + 1, airplane, end, start
                );
                puts.add(constructPutFromFlight(backFlight));
                if (puts.size() >= 1000) {
                    table.put(puts);
                    System.out.println("puts " + puts.size() + " flights to HBase");
                    puts.clear();
                }
                flightId += 2;
                airplaneId++;
            }
        }
        System.out.println("puts " + puts.size() + " flights to HBase");
        table.put(puts);
    }
}
