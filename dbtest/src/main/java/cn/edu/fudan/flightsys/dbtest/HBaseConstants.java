package cn.edu.fudan.flightsys.dbtest;

/**
 * Created by junfeng on 12/5/15.
 */
public class HBaseConstants {
    public static final String FLIGHT_NAMESPACE = "flight";
    public static final String USER_TABLE_NAME = "user";
    public static final String FLIGHT_TABLE_NAME = "flight";
    public static final String ORDER_TABLE_NAME = "order";
    public static final String USERS_FILE = "./datasets/unique_names.csv";
    public static final String CITIES_FILE = "./datasets/areas_pd.csv";
    public static final String[] CITIES_FILE_HEADER = {
            "id", "areaname",
            "parentid", "shortname",
            "lng", "lat", "level",
            "position", "sort"};
    public static final String EMAIL_SUFFIX = "@fudan.edu.cn";
    public static final int MAX_FLIGHT_DATE = (9999 << 16) + (12 << 8) + 31;
}
