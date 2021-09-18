package com.example.admin.utils;

import com.example.admin.config.RedisConfig;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class RedisUtils {
    private static JedisPool jedisPool;

    @Autowired
    private RedisConfig redisConfig;

    /**
     * JedisPool 无法通过@Autowired注入，可能由于是方法bean的原因，此处可以先注入RedisConfig，
     * 然后通过@PostConstruct初始化的时候将factory直接赋给jedisPool
     */
    @PostConstruct
    public void init() {
        jedisPool = redisConfig.redisPoolFactory();
    }

    @ApiOperation("获取 jedis 实例来操作数据，每次使用完要将连接返回给连接池 jedis.close()")
    public synchronized static Jedis getRedis() {
        try {
            if (jedisPool != null) {
                //获取 jedis 实例
                return jedisPool.getResource();
            } else {
                log.info("没有找到 Jedis 连接池！");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
