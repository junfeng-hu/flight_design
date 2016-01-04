package cn.edu.fudan.flightweb.controller;

import cn.edu.fudan.flightweb.db.Redis;
import cn.edu.fudan.flightweb.domain.Flight;
import cn.edu.fudan.flightweb.domain.OrderFlight;
import cn.edu.fudan.flightweb.interceptor.Authenticated;
import cn.edu.fudan.flightweb.util.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.GeneralSecurityException;
import java.sql.Date;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by junfeng on 9/12/15.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {
    /**
     * meta cookie key
     */
    public static final String COOKIE_KEY = "META_SESSION";
    public static final String SESSION_USER = "META_USER";

    private static final Logger logger =
            LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister() {
        return "user/register";
    }

    /**
     * check user exists
     * @param username
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public MetaResult checkUserExists(@RequestParam String username) {
        MetaResult result = new MetaResult();
        Redis redis = Redis.getInstance();
        if (redis.checkUserExists(username)) {
            result.setMessage(username + " don't exists");
        }
        else {
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage(username + " already exists");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public MetaResult handleRegister(@RequestParam String username,
                                     @RequestParam String password,
                                     @CookieValue(value = COOKIE_KEY, required = false) String sessionId,
                                     HttpSession session) throws GeneralSecurityException {
        MetaResult result = new MetaResult();
        // check user already login
        if(null != sessionId) {
            Map<String, String> sessionUser = (Map<String, String>) session.getAttribute(SESSION_USER);
            if (null == sessionUser) {
                Redis redis = Redis.getInstance();
                if (redis.checkSessionExists(sessionId)) {
                    sessionUser = redis.getSessionUser(sessionId);
                    session.setAttribute(SESSION_USER, sessionUser);
                }
            }
            if (null != sessionUser) {
                // already login
                result.setStatus(MetaResult.Status.ERROR);
                result.setMessage("you already login as " + sessionUser.get("username"));
                return result;
            }
        }
        if (username.isEmpty() || password.isEmpty()) {
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("username and password can't empty");
            return result;
        }
        // check username
        if (!Pattern.matches("^[\\w-]{4,21}$", username)) {
            // illegal username
            // need first put admin accounts at DB, here doesn't check
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("username " + username + " is  illegal, " +
                    "should match \"^[\\w-]{4,21}$\" pattern");
            return result;
        }
        Redis redis = Redis.getInstance();
        if(redis.checkUserExists(username)) {
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage(username + " already exists");
            return result;
        }
        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("password", Password.getSaltedHash(password));
        user.put("role", "user");
        user.put("point", String.valueOf(0));
        redis.saveUser(username, user);
        result.setStatus(MetaResult.Status.SUCCESS);
        result.setMessage("successfully register user " + username);
        return result;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLogin(HttpServletRequest request) {
         /* reflector test
        Class<?> cls = Class.forName("PrintClasspath");
        Object obj = cls.getConstructor().newInstance();
         */
        return "user/login";
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public MetaResult handleLogin(@RequestParam String username,
                                  @RequestParam String password,
                                  @CookieValue(value = COOKIE_KEY, required = false) String sessionId,
                                  HttpServletResponse response,
                                  HttpSession session) throws GeneralSecurityException {
        MetaResult result = new MetaResult();
        // check user already login
        if(null != sessionId) {
            Map<String, String> sessionUser = (Map<String, String>) session.getAttribute(SESSION_USER);
            if (null == sessionUser) {
                Redis redis = Redis.getInstance();
                if (redis.checkSessionExists(sessionId)) {
                    sessionUser = redis.getSessionUser(sessionId);
                    session.setAttribute(SESSION_USER, sessionUser);
                }
            }
            if (null != sessionUser) {
                // already login
                result.setStatus(MetaResult.Status.ERROR);
                result.setMessage("you already login as " + sessionUser.get("username"));
                return result;
            }
        }
        // check username and password
        if (username.isEmpty() || password.isEmpty()) {
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("username and password can't empty");
            return result;
        }
        Redis redis = Redis.getInstance();
        if (!redis.checkUserExists(username)) {
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("user " + username + " doesn't exists");
            return result;
        }
        Map<String, String> user = redis.getUser(username);
        if (Password.check(password, user.get("password"))) {
            // login successfully, set cookie, add user to session
            logger.info("user {} successfully login to metadata", username);
            sessionId = UUID.randomUUID().toString();
            session.setAttribute(SESSION_USER, user);
            Cookie cookie = new Cookie(COOKIE_KEY, sessionId);
            cookie.setMaxAge(3600 * 24 * 30);
            // cookie.setDomain();
            cookie.setPath("/");
            response.addCookie(cookie);
            redis.saveSessionUser(sessionId, user);
            result.setStatus(MetaResult.Status.SUCCESS);
            result.setMessage(username + " successfully login");
        }
        else {
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("username or password is not correct");
        }
        return result;
    }

    @Authenticated(Authenticated.AuthType.PAGE)
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String handleLogout(@CookieValue(COOKIE_KEY) Cookie cookie,
                               HttpServletResponse response,
                               HttpSession session) {

        Redis.getInstance().removeSessionUser(cookie.getValue());
        // cookie need same properties except value as handleLogin
        // then can delete it
        cookie.setValue(null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        session.removeAttribute(SESSION_USER);
        // session.invalidate();
        return "redirect:/user/login";
    }

    @Authenticated(Authenticated.AuthType.PAGE)
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String showInfoPage(HttpSession session) {

        // Map<String, String> user = (Map<String, String>) session.getAttribute(SESSION_USER);
        return "user/info";
    }

    @Authenticated(Authenticated.AuthType.PAGE)
    @RequestMapping(value = "/change", method = RequestMethod.GET)
    public String showChangePage(HttpSession session) {
        // Map<String, String> user = (Map<String, String>) session.getAttribute(SESSION_USER);
        return "user/change";
    }

    @Authenticated(Authenticated.AuthType.JSON)
    @ResponseBody
    @RequestMapping(value = "/change", method = RequestMethod.POST)
    public MetaResult handleChangePassword(@RequestParam("cur") String curPassword,
                                           @RequestParam("new") String newPassword,
                                           @CookieValue(COOKIE_KEY) Cookie cookie,
                                           HttpServletResponse response,
                                           HttpSession session) throws GeneralSecurityException {

        Map<String, String> user = (Map<String, String>) session.getAttribute(SESSION_USER);
        MetaResult result = new MetaResult();
        // check username and password
        if (curPassword.isEmpty() || newPassword.isEmpty()) {
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("current and new password can't empty");
            return result;
        }
        String username = user.get("username");
        Redis redis = Redis.getInstance();
        if (Password.check(curPassword, user.get("password"))) {
            // password match
            // change password
            user.put("password", Password.getSaltedHash(newPassword));
            redis.saveUser(username, user);
            // handleLogout(cookie, response, session);
            logger.info("user {} successfully changed password", username);
            result.setStatus(MetaResult.Status.SUCCESS);
            result.setMessage("successfully changed password");
        }
        else {
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("current password is not correct");
        }
        return result;
    }

    @Authenticated(Authenticated.AuthType.PAGE)
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public String showHistoryPage(HttpSession session) {
        // Map<String, String> user = (Map<String, String>) session.getAttribute(SESSION_USER);
        return "user/order";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @Authenticated(Authenticated.AuthType.JSON)
    @ResponseBody
    List<OrderFlight> handleFindOrder(@RequestParam Date startDate,
                                      @RequestParam Date endDate,
                                      HttpSession session) {
        Map<String, String> user = (Map<String, String>) session.getAttribute(SESSION_USER);
        List<OrderFlight> orders = new ArrayList<>();
        // TODO handle history orders search
        return orders;
    }
}
