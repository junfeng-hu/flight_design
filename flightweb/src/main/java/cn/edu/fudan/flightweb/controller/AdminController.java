package cn.edu.fudan.flightweb.controller;

import cn.edu.fudan.flightweb.domain.Flight;
import cn.edu.fudan.flightweb.interceptor.Authenticated;
import cn.edu.fudan.flightweb.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by junfeng on 1/2/16.
 */
@Controller
@Authenticated(role = Authenticated.RoleType.ADMIN)
@RequestMapping(value = "/admin")
public class AdminController {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminController.class);

    @ResponseBody
    @RequestMapping(value = "/{username}", method = {RequestMethod.POST, RequestMethod.DELETE})
    public MetaResult deleteUser(@PathVariable String username, HttpSession session) {
        Map<String, String> user = (Map<String, String>) session.getAttribute(UserController.SESSION_USER);
        MetaResult result = new MetaResult();
        // TODO apply real delete
        logger.info("admin {} deleted user {}", user.get("username"), username);
        result.setMessage("successfully delete user " + username);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/{username}", method = {RequestMethod.GET})
    public Map<String, String> getUserInfo(@PathVariable String username, HttpSession session) {
        return (Map<String, String>) session.getAttribute(UserController.SESSION_USER);
    }

    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET})
    public List<Map<String, String>> listAllUsers(HttpSession session) {
        Map<String, String> user = (Map<String, String>) session.getAttribute(UserController.SESSION_USER);
        return new ArrayList<>(Arrays.asList(user));
    }

    @Authenticated(value = Authenticated.AuthType.PAGE, role = Authenticated.RoleType.ADMIN)
    @RequestMapping(value = "/flight", method = {RequestMethod.GET})
    String showAddFlightPage() {
        return "admin/flight";
    }

    @Authenticated(value = Authenticated.AuthType.JSON, role = Authenticated.RoleType.ADMIN)
    @RequestMapping(value = "/flight", method = {RequestMethod.POST})
    @ResponseBody
    MetaResult showAddFlightPage(Flight flight) {
        MetaResult result = new MetaResult();
        // logger.warn(flight.toString());
        flightRepository.save(flight);
        result.setStatus(MetaResult.Status.SUCCESS);
        result.setMessage("successfully added the flight");
        return result;
    }

    @Autowired
    private FlightRepository flightRepository;

}
