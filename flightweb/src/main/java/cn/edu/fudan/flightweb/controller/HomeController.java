package cn.edu.fudan.flightweb.controller;

/**
 * Created by junfeng on 7/30/15.
 */

import cn.edu.fudan.flightweb.db.Redis;
import cn.edu.fudan.flightweb.interceptor.Authenticated;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * site/: using resources/static/tempfile.html
 */
@Controller
@RequestMapping("/")
public class HomeController {
    //@Authenticated(Authenticated.AuthType.PAGE)
    @RequestMapping(method = RequestMethod.GET)
    String index(@CookieValue(value = UserController.COOKIE_KEY, required = false) String sessionId,
                 HttpSession session,
                 Model model) {
        // check user already login
        if(null != sessionId) {
            Map<String, String> sessionUser = (Map<String, String>) session.getAttribute(UserController.SESSION_USER);
            if (null == sessionUser) {
                Redis redis = Redis.getInstance();
                if (redis.checkSessionExists(sessionId)) {
                    // fetch sessionUser from Redis
                    sessionUser = redis.getSessionUser(sessionId);
                    session.setAttribute(UserController.SESSION_USER, sessionUser);
                }
                else {
                    // need clear COOKIE_KEY

                }
            }
        }
       return "index";
   }
}
