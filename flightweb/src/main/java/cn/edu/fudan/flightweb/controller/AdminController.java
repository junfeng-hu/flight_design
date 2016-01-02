package cn.edu.fudan.flightweb.controller;

import cn.edu.fudan.flightweb.interceptor.Authenticated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by junfeng on 1/2/16.
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminController.class);

    @Authenticated(role = Authenticated.RoleType.ADMIN)
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

    @Authenticated(role = Authenticated.RoleType.ADMIN)
    @ResponseBody
    @RequestMapping(value = "/{username}", method = {RequestMethod.GET})
    public Map<String, String> getUserInfo(@PathVariable String username, HttpSession session) {
        return (Map<String, String>) session.getAttribute(UserController.SESSION_USER);
    }

    @Authenticated(role = Authenticated.RoleType.ADMIN)
    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET})
    public List<Map<String, String>> listAllUsers(HttpSession session) {
        Map<String, String> user = (Map<String, String>) session.getAttribute(UserController.SESSION_USER);
        return new ArrayList<>(Arrays.asList(user));
    }

}
