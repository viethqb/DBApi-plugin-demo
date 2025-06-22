package com.gitee.freakchicken.demo.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.freakchicken.dbapi.common.ApiConfig;
import com.gitee.freakchicken.dbapi.plugin.CachePlugin;
import com.gitee.freakchicken.dbapi.plugin.PluginConf;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;

public class RedisCachePlugin extends CachePlugin {

    JedisPool pool;

    public void testCollection() {
        Jedis resource = pool.getResource();
    }

    @Override
    public void init() {
        JedisPoolConfig jcon = new JedisPoolConfig();
        jcon.setMaxTotal(200);
        jcon.setMaxIdle(50);
        jcon.setTestOnBorrow(true);
        jcon.setTestOnReturn(true);
        String password = PluginConf.getKey("RedisCachePlugin.password");
        if (StringUtils.isNotBlank(password)) {
            this.pool = new JedisPool(jcon, PluginConf.getKey("RedisCachePlugin.ip"),
                    Integer.parseInt(PluginConf.getKey("RedisCachePlugin.port")), 100,
                    password,
                    Integer.parseInt(PluginConf.getKey("RedisCachePlugin.db")));
        } else {
            this.pool = new JedisPool(jcon, PluginConf.getKey("RedisCachePlugin.ip"),
                    Integer.parseInt(PluginConf.getKey("RedisCachePlugin.port")), 100,
                    null,
                    Integer.parseInt(PluginConf.getKey("RedisCachePlugin.db")));
        }
        super.logger.info("init jedis pool success");
    }

    @Override
    public void set(ApiConfig apiConfig, Map<String, Object> requestParams, Object data, String localPluginParam) {
        // redis caching time
        String expireTime = localPluginParam;
        super.logger.debug("set data to cache");
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String key = "api-" + apiConfig.getId();
            String hashKey = "";
            for (Object o : requestParams.values()) {
                hashKey += o.toString() + "-";
            }
            jedis.hset(key, hashKey, JSON.toJSONString(data));
            // Set the expiration time. The expiration time is passed from the plug-in parameters.
            if (StringUtils.isNoneBlank(expireTime)) {
                jedis.expire(key, Integer.parseInt(expireTime));
            }
        } catch (Exception e) {
            super.logger.error("Setting cache failed", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void clean(ApiConfig config, String localPluginParam) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String key = "api-" + config.getId();
            jedis.del(key);
        } catch (Exception e) {
            super.logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Object get(ApiConfig config, Map<String, Object> requestParams, String localPluginParam) {
        super.logger.debug("get data from cache");
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String key = "api-" + config.getId();
            String hashKey = "";
            for (Object o : requestParams.values()) {
                hashKey += o.toString() + "-";
            }
            String hget = jedis.hget(key, hashKey);
            List<JSONObject> list = JSON.parseArray(hget, JSONObject.class);
            return list;
        } catch (Exception e) {
            super.logger.error("Query cache failed", e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * Plug-in name, used to display on the page to prompt the user
     *
     * @return
     */
    @Override
    public String getName() {
        return "RedisCachePlugin";
    }

    /**
     * Plug-in function description, used to display on the page to prompt the user
     *
     * @return
     */
    @Override
    public String getDescription() {
        return "Cache to redis";
    }

    /**
     * Plug-in parameter description, used to display on the page and prompt the user
     *
     * @return
     */
    @Override
    public String getParamDescription() {
        return "Please fill in the redis cache aging time for the plug-in parameters. If not filled in, it means permanent caching.";
    }
}
