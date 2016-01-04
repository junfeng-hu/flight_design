package cn.edu.fudan.flightweb.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;

/**
 * Created by junfeng on 1/4/16.
 */
@Entity
@Table(name = "flight",
        indexes = {
                @Index(name = "flight_unique", columnList = "flightNumber,flightDate", unique = true),
                @Index(name = "search_index", columnList = "flightDate,startPlace,arrivalPlace")
        })
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String flightNumber;

    @NotNull
    private Date flightDate;

    @NotNull
    private String startPlace;

    @NotNull
    private String arrivalPlace;

    @NotNull
    private String startTime;

    @NotNull
    private String arrivalTime;

    private int firstCount;

    private int economyCount;

    private Double firstPrice;

    private Double economyPrice;

    protected Flight() {}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public void setArrivalPlace(String arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getFirstCount() {
        return firstCount;
    }

    public void setFirstCount(int firstCount) {
        this.firstCount = firstCount;
    }

    public int getEconomyCount() {
        return economyCount;
    }

    public void setEconomyCount(int economyCount) {
        this.economyCount = economyCount;
    }

    public Double getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(Double firstPrice) {
        this.firstPrice = firstPrice;
    }

    public Double getEconomyPrice() {
        return economyPrice;
    }

    public void setEconomyPrice(Double economyPrice) {
        this.economyPrice = economyPrice;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", flightNumber='" + flightNumber + '\'' +
                ", flightDate=" + flightDate +
                ", startPlace='" + startPlace + '\'' +
                ", arrivalPlace='" + arrivalPlace + '\'' +
                ", startTime=" + startTime +
                ", arrivalTime=" + arrivalTime +
                ", firstCount=" + firstCount +
                ", economyCount=" + economyCount +
                ", firstPrice=" + firstPrice +
                ", economyPrice=" + economyPrice +
                '}';
    }
}
