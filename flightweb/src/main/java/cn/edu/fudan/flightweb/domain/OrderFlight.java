package cn.edu.fudan.flightweb.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by junfeng on 1/4/16.
 */
@Entity
@Table(name = "order_flight",
        indexes = {
                @Index(name = "search_history", columnList = "orderUser,flightDate")
        })
public class OrderFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String orderUser;

    @NotNull
    private String flightNumber;

    @NotNull
    private Date flightDate;

    private String startPlace;

    private String arrivalPlace;

    private String startTime;

    private String arrivalTime;

    private Boolean isFirst;

    private Double price;

    private Boolean paid;

    @ElementCollection
    List<String> passengers = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(String orderUser) {
        this.orderUser = orderUser;
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

    public Boolean getFirst() {
        return isFirst;
    }

    public void setFirst(Boolean first) {
        isFirst = first;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "OrderFlight{" +
                "id=" + id +
                ", orderUser='" + orderUser + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", flightDate=" + flightDate +
                ", startPlace='" + startPlace + '\'' +
                ", arrivalPlace='" + arrivalPlace + '\'' +
                ", startTime='" + startTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", isFirst=" + isFirst +
                ", price=" + price +
                ", paid=" + paid +
                ", passengers=" + passengers +
                '}';
    }
}
