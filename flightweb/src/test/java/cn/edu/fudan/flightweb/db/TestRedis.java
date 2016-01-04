package cn.edu.fudan.flightweb.db;

import cn.edu.fudan.flightweb.util.Password;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by junfeng on 1/3/16.
 */
public class TestRedis {
    @Test
    public void testAddAdmin() throws Exception {
        Map<String, String> admin = new HashMap<>();
        String username = "admin";
        admin.put("username", username);
        admin.put("password", Password.getSaltedHash(username));
        admin.put("role", "admin");
        admin.put("point", String.valueOf(0));
        boolean ret = Redis.getInstance().saveUser(username, admin);
        Assert.assertTrue(ret);

    }
}
