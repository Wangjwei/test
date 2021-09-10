package com.example.redis;

import com.example.common.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest
class RedisApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testRedis(){
        Jedis jedis1 = RedisUtils.getRedis();
        Jedis jedis2 = RedisUtils.getRedis();
        Jedis jedis3 = RedisUtils.getRedis();
        Jedis jedis4 = RedisUtils.getRedis();
        Jedis jedis5 = RedisUtils.getRedis();

        jedis1.close();
        Jedis jedis6 = RedisUtils.getRedis();

        System.out.println("jedis2- "+jedis2.getClient());
        System.out.println("jedis3- "+jedis3.toString());
        System.out.println("jedis4- "+jedis4.ping());
        System.out.println("jedis5- "+jedis5.clientGetname());
        System.out.println("jedis6- "+jedis6.info());
    }

}
