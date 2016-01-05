package cn.edu.fudan.flightweb.domain;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by junfeng on 1/5/16.
 */
@Embeddable
public class Passenger {

    private String name;
    private String flightNumber;
    private Date flightDate;

    protected Passenger() {}

    public Passenger(String name, String flightNumber, Date flightDate) {
        this.name = name;
        this.flightNumber = flightNumber;
        this.flightDate = flightDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }
}
