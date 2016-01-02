package cn.edu.fudan.flightweb.interceptor;

import cn.edu.fudan.flightweb.controller.MetaResult;
import cn.edu.fudan.flightweb.db.Redis;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import static cn.edu.fudan.flightweb.controller.UserController.COOKIE_KEY;
import static cn.edu.fudan.flightweb.controller.UserController.SESSION_USER;

/**
 * Created by junfeng on 9/13/15.
 */
@Component
public class AuthenticateInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger =
            LoggerFactory.getLogger(AuthenticateInterceptor.class);

    /**
     * doesn't need it
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * automatic handle authentication
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.preHandle(request, response, handler);
        HandlerMethod handlerMethod;
        try {
            handlerMethod = (HandlerMethod) handler;
        }
        catch (ClassCastException e) {
            return true;
        }
        //method is primary, can rewrite class annotation
        Authenticated login = handlerMethod.getMethodAnnotation(Authenticated.class);
        Authenticated classLogin = handlerMethod.getBean().getClass().getAnnotation(Authenticated.class);
        if (null == login && null == classLogin) {
            return true;
        }
        if (null == login) {
            login = classLogin;
        }
        // get user to check role
        HttpSession session = request.getSession();
        Map<String, String> user = (Map<String, String>) session.getAttribute(SESSION_USER);
        if (null == user) {
            // get sessionId
            String sessionId = null;
            Cookie sessionCookie = null;
            if (null != request.getCookies()) {
                // fix null point error
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals(COOKIE_KEY)) {
                        sessionId = cookie.getValue();
                        sessionCookie = cookie;
                        break;
                    }
                }
            }
            // if sessionId != null and sessionUser == null,
            // save sessionUser to HttpSession,
            // else need authenticate first, clear sessionId Cookie if exists
            if (sessionId != null) {
                Redis redis = Redis.getInstance();
                if (redis.checkSessionExists(sessionId)) {
                    user = redis.getSessionUser(sessionId);
                    session.setAttribute(SESSION_USER, user);
                }
                else {
                    sessionCookie.setValue(null);
                    sessionCookie.setMaxAge(0);
                    sessionCookie.setPath("/");
                    response.addCookie(sessionCookie);
                }
            }
        }

        // check proper role
        if (null != user) {
            if (login.role() == Authenticated.RoleType.USER) {
                return true;
            }
            else if ( user.get("role").equals("admin")) {
                return true;
            }
        }

        // need proper role login
        if (login.value() == Authenticated.AuthType.PAGE) {
            //forward to login page
            request.getRequestDispatcher("/user/login").forward(request, response);
        } else if (login.value() == Authenticated.AuthType.JSON) {
            //REST API need authenticated first
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=UTF-8");
            OutputStream out = response.getOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out,"utf-8"));
            // json format
            MetaResult result = new MetaResult();
            result.setStatus(MetaResult.Status.ERROR);
            result.setMessage("the url need proper role authenticated first");
            ObjectMapper objectMapper = new ObjectMapper();
            pw.println(objectMapper.writeValueAsString(result));
            pw.flush();
            pw.close();
        }
        return false;
    }
}
