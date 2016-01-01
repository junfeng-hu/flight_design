package cn.edu.fudan.flightsys.pojo;

/**
 * Created by junfeng on 12/5/15.
 */
public class City {

    private String cityName;
    /**
     * longitude
     */
    private double lng;
    /**
     * latitude
     */
    private double lat;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityName='" + cityName + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
