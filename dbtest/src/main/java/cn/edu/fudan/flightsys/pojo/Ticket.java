package cn.edu.fudan.flightsys.pojo;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by junfeng on 12/5/15.
 */
public class Ticket {
    private Flight flight;
    /**
     * high two bits is year 0-Short.MAX_VALUE
     * second low bit is month 1-12
     * low bit is day 1-31
     */
    private int flightDate;
    private int firstCount;
    private double firstPrice;
    private int economyCount;
    private double economyPrice;

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public int getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(int flightDate) {
        this.flightDate = flightDate;
    }

    public int getFirstCount() {
        return firstCount;
    }

    public void setFirstCount(int firstCount) {
        this.firstCount = firstCount;
    }

    public double getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(double firstPrice) {
        this.firstPrice = firstPrice;
    }

    public int getEconomyCount() {
        return economyCount;
    }

    public void setEconomyCount(int economyCount) {
        this.economyCount = economyCount;
    }

    public double getEconomyPrice() {
        return economyPrice;
    }

    public void setEconomyPrice(double economyPrice) {
        this.economyPrice = economyPrice;
    }


    public static String getPrintableDate(int d) {
        int year = d >>> 16;
        int month = (d & 0x0000FF00) >>> 8;
        int day = d & 0x000000FF;
        return String.format("%d-%d-%d", year, month, day);
    }

    public static int getIntValueFromLocalDate(LocalDate date) {
        return (date.getYear() << 16) + (date.getMonthValue() << 8) + date.getDayOfMonth();
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "flight=" + flight +
                ", flightDate=" + getPrintableDate(flightDate) +
                ", firstCount=" + firstCount +
                ", firstPrice=" + firstPrice +
                ", economyCount=" + economyCount +
                ", economyPrice=" + economyPrice +
                '}';
    }
}
