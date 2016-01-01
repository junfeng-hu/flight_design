package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.City;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by junfeng on 12/13/15.
 */
public class DataProvider {
    private static DataProvider ourInstance = new DataProvider();

    public static DataProvider getInstance() {
        return ourInstance;
    }
    private int nCity;
    private City[] cities;
    private int[][] toIndex;
    private String[] users;
    private int nUser;
    private DataProvider() {
        try {
            List<City> cityList = HBaseFlightTableImporter.readCitiesFromCsv();
            List<String> userList = HBaseUserTableImporter.readUsersFromCsv();
            nCity = cityList.size();
            nUser = userList.size();
            cities = cityList.toArray(new City[nCity]);
            users = userList.toArray(new String[nUser]);
            toIndex = constructToIndexMap(nCity);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("read CSV data error", e);
        }
    }

    private static int[][] constructToIndexMap(int n) {
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

    public String sampleAUser() {
        int i = ThreadLocalRandom.current().nextInt(nUser);
        return users[i];
    }

    public City getCityUseIndex(int index) {
        return cities[index];
    }

    public int sampleACityIndex() {
        return ThreadLocalRandom.current().nextInt(nCity);
    }

    public int sampleNextCityIndex(int startCityIndex) {
        int i = ThreadLocalRandom.current().nextInt(nCity - 1);
        return toIndex[startCityIndex][i];
    }


}
