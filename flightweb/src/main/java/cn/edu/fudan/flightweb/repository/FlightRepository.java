package cn.edu.fudan.flightweb.repository;

import cn.edu.fudan.flightweb.domain.Flight;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

/**
 * Created by junfeng on 1/4/16.
 */
public interface FlightRepository extends CrudRepository<Flight, Long> {
    List<Flight> findByFlightDateAndStartPlaceAndArrivalPlace(
            Date flightDate, String startPlace, String arrivalPlace);

}
