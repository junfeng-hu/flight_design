package cn.edu.fudan.flightweb.controller;

import cn.edu.fudan.flightweb.domain.Flight;
import cn.edu.fudan.flightweb.interceptor.Authenticated;
import cn.edu.fudan.flightweb.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by junfeng on 9/12/15.
 */
@Controller
@RequestMapping(value = "/ticket")
public class TicketController {

    private static final Logger logger =
            LoggerFactory.getLogger(TicketController.class);



    @RequestMapping(value = "/find", method = RequestMethod.GET)
    String showFindTicketPage() {

        return "ticket/find";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    List<Flight> handleFindTicket(@RequestParam Date flightDate,
                                  @RequestParam String startPlace,
                                  @RequestParam String arrivalPlace) {
        List<Flight> flights = flightRepository.findByFlightDateAndStartPlaceAndArrivalPlace(
                flightDate, startPlace, arrivalPlace
        );

        return flights;
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @Authenticated
    @ResponseBody
    MetaResult handleOrderTicket(@RequestParam int flightId,
                                  @RequestParam String passengers,
                                  @RequestParam Boolean isFirst) {
        MetaResult result = new MetaResult();
        // TODO handle order logical
        return result;
    }

    @Autowired
    private FlightRepository flightRepository;
}
