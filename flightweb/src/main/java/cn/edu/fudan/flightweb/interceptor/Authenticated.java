package cn.edu.fudan.flightweb.interceptor;

import java.lang.annotation.*;

/**
 * Created by junfeng on 9/13/15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface Authenticated {
    enum AuthType {
        PAGE,
        JSON
    }
    enum RoleType {
        USER,
        ADMIN
    }
    AuthType value() default AuthType.JSON;
    RoleType role() default RoleType.USER;
}
