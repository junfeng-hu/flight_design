package cn.edu.fudan.flightweb.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.*;

/**
 * Created by junfeng on 8/11/15.
 */

/**
 * a singleton for communicate with Redis server using JedisPool
 * https://en.wikipedia.org/wiki/Singleton_pattern
 * I prepare to use double-checked locking because We are already arriving at Java 1.8
 * But IntelliJ IDEA already choose Eager initialization
 * add final to respect Wikipedia
 */
public class Redis {
    private static final Logger logger =
            LoggerFactory.getLogger(Redis.class);

    private static final String userSetKey = "user";
    private static final String userKeyPrefix = userSetKey + ":";
    private static final String sessionKeyPrefix = "session:";
    private static final String HOST = "10.131.244.41";

    private static final Redis ourInstance = new Redis();
    public static Redis getInstance() {
        return ourInstance;
    }

    private JedisPool pool = new JedisPool(HOST);
    private Redis() {
    }

    /**
     * check user exists
     * @param username
     * @return true if exists
     */
    public boolean checkUserExists(String username) {
        try(Jedis jedis = pool.getResource()) {
            return jedis.exists(userKeyPrefix + username);
        }
        catch (Exception e) {
            logger.warn("when check user {} exists", username, e);
            throw e;
        }
    }

    /**
     * save user info to Redis
     * @param username
     * @param hash user info
     * @return true for success
     */
    public boolean saveUser(String username, Map<String, String> hash) {
        try(Jedis jedis = pool.getResource()) {
            Date registerDate = new Date();
            hash.put("registerTime", registerDate.toString());
            jedis.hmset(userKeyPrefix + username, hash);
            jedis.zadd(userSetKey, registerDate.getTime(), username);
            return true;
        }
        catch (Exception e) {
            logger.warn("when save user {} to Redis", username, e);
            throw e;
        }
    }

    /**
     * get user's detail info
     * @param username
     * @return
     */
    public Map<String, String> getUser(String username) {
        try(Jedis jedis = pool.getResource()) {
            return jedis.hgetAll(userKeyPrefix + username);
        }
        catch (Exception e) {
            logger.warn("when get user {} from Redis", username, e);
            throw e;
        }
    }

    /**
     * get all users' info
     * @return
     */
    public List<Map<String, String>> getAllUsers() {
        try(Jedis jedis = pool.getResource()) {
            Set<String> usersId = jedis.zrange(userSetKey, 0, -1);
            List<Map<String, String>> users = new ArrayList<>(usersId.size());
            for (String userId : usersId) {
                Map<String, String> user = jedis.hgetAll(userKeyPrefix + userId);
                users.add(user);
            }
            return users;
        }
        catch (Exception e) {
            logger.warn("when get all users' info from Redis", e);
            throw e;
        }
    }

    /**
     * get session user
     * same as user's info
     * and refresh expire time
     * @param sessionId
     * @return
     */
    public Map<String, String> getSessionUser(String sessionId) {
        try(Jedis jedis = pool.getResource()) {
            jedis.expire(sessionKeyPrefix + sessionId, 3600 * 24);
            return  jedis.hgetAll(sessionKeyPrefix + sessionId);
        }
        catch (Exception e) {
            logger.warn("when get session {} from Redis", sessionId, e);
            throw e;
        }
    }

    /**
     * check session user associated exists
     * @param sessionId key to check
     * @return
     */
    public boolean checkSessionExists(String sessionId) {
        try(Jedis jedis = pool.getResource()) {
            return jedis.exists(sessionKeyPrefix + sessionId);
        }
        catch (Exception e) {
            logger.warn("when check session {} exists in Redis", sessionId, e);
            throw e;
        }
    }

    /**
     * delete session user
     * @param sessionId
     * @return
     */
    public boolean removeSessionUser(String sessionId) {
        try(Jedis jedis = pool.getResource()) {
            return jedis.del(sessionKeyPrefix + sessionId) == 1;
        }
        catch (Exception e) {
            logger.warn("when remove session {} exists in Redis", sessionId, e);
            throw e;
        }
    }

    /**
     * save session user
     * set expire time one day
     * automatically delete session user after expired
     * @param sessionId
     * @param hash
     * @return
     */
    public boolean saveSessionUser(String sessionId, Map<String, String> hash) {
        try(Jedis jedis = pool.getResource()) {
            jedis.hmset(sessionKeyPrefix + sessionId, hash);
            jedis.expire(sessionKeyPrefix + sessionId, 3600 * 24);
            return true;
        }
        catch (Exception e) {
            logger.warn("when save session {} to Redis", sessionId, e);
            throw e;
        }
    }

}
