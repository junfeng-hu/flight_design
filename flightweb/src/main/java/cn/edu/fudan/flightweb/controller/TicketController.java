package cn.edu.fudan.flightweb.controller;

import cn.edu.fudan.flightweb.domain.Flight;
import cn.edu.fudan.flightweb.domain.Passenger;
import cn.edu.fudan.flightweb.interceptor.Authenticated;
import cn.edu.fudan.flightweb.repository.FlightRepository;
import cn.edu.fudan.flightweb.repository.OrderFlightRepository;
import cn.edu.fudan.flightweb.service.OrderFlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    MetaResult handleOrderTicket(@RequestParam long flightId,
                                 @RequestParam String passengers,
                                 @RequestParam boolean isFirst,
                                 HttpSession session) {
        MetaResult result = new MetaResult();
        // done handle order logical
        Map<String, String> user = (Map<String, String>) session.getAttribute(UserController.SESSION_USER);
        List<String> passengerL = Arrays.asList(passengers.split("&"));
        boolean ret = orderFlightService.doOrderFlight(flightId, isFirst,
                user.get("username"), passengerL);
        if (!ret) {
            // order fail
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("预订失败，请刷新页面");
        }
        else {
            // success
            result.setStatus(MetaResult.Status.SUCCESS);
            result.setMessage("预订成功，请查看历史订单");
        }
        return result;
    }

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private OrderFlightService orderFlightService;
}
