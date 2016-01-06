package cn.edu.fudan.flightweb.service;

import cn.edu.fudan.flightweb.domain.Flight;
import cn.edu.fudan.flightweb.domain.OrderFlight;
import cn.edu.fudan.flightweb.domain.Passenger;
import cn.edu.fudan.flightweb.repository.FlightRepository;
import cn.edu.fudan.flightweb.repository.OrderFlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junfeng on 1/5/16.
 */
@Service
@Transactional
public class OrderFlightService {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private OrderFlightRepository orderFlightRepository;

    public Double doOrderFlight(long flightId, boolean isFirst,
                                 String orderUser, List<String> passengers) {
        Flight flight = flightRepository.findOne(flightId);
        Double price;
        if (isFirst) {
            if (flight.getFirstCount() < passengers.size()) {
                // first tickets not enough
                return -1d;
            }
            flight.setFirstCount(flight.getFirstCount() - passengers.size());
            price = flight.getFirstPrice() * passengers.size();
        }
        else {
            if (flight.getEconomyCount() < passengers.size()) {
                // economy tickets not enough
                return -1d;
            }
            flight.setEconomyCount(flight.getEconomyCount() - passengers.size());
            price = flight.getEconomyPrice() * passengers.size();
        }
        OrderFlight orderFlight = new OrderFlight(flight);
        List<Passenger> passengerL = new ArrayList<>(passengers.size());
        for (String name : passengers) {
            passengerL.add(new Passenger(name, flight.getFlightNumber(), flight.getFlightDate()));
        }
        orderFlight.setPassengers(passengerL);
        orderFlight.setOrderUser(orderUser);
        orderFlight.setFirst(isFirst);
        orderFlight.setPaid(true);
        orderFlight.setPrice(price);
        orderFlightRepository.save(orderFlight);
        flightRepository.save(flight);
        return price;
    }
}
