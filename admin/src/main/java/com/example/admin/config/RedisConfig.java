package com.example.admin.config;

import io.netty.util.internal.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
public class RedisConfig {

    @ApiModelProperty("IP地址")
    @Value("${spring.redis.host}")
    private String HOST;

    @ApiModelProperty("端口号")
    @Value("${spring.redis.port}")
    private int PORT;

    @ApiModelProperty("redis服务端密码")
    @Value("${spring.redis.password}")
    private String PASSWORD;

    @ApiModelProperty("可用连接实例最大数目，默认为 8，若赋值 -1，表示不被限制")
    @Value("${spring.redis.jedis.pool.max-active}")
    private Integer MAX_TOTAL;

    @ApiModelProperty("控制一个连接池最多有多少个状态为空闲的 jedis 实例，默认值为 8")
    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer MAX_IDLE;

    @ApiModelProperty("最小空闲连接数")
    @Value("${spring.redis.jedis.pool.min-idle}")
    private Integer MIN_IDLE;

    @ApiModelProperty("等待可用连接最大的等待时间，单位 ms，默认值 -1，表示永不超时，若等待超时抛出 JedisConnectionException")
    @Value("${spring.redis.jedis.pool.max-wait}")
    private Integer MAX_WAIT_MILLIS;

    @ApiModelProperty("超时时间")
    @Value("${spring.redis.timeout}")
    private Integer TIMEOUT;

    public JedisPool redisPoolFactory() {
        JedisPoolConfig config = new JedisPoolConfig();
        /*
         * 高版本 jedis jar 中 JedisPoolConfig 没有 setMaxActive 和 setMaxWait 属性，因为官方在高版本			   * 中启用了此方法，用以下两个属性替换
         * maxActive ==> maxTotal
         * maxWait ==> maxWaitMillis
         */
        //最大连接数
        config.setMaxTotal(MAX_TOTAL);
        //最大空闲连接数
        config.setMaxIdle(MAX_IDLE);
        //最小空闲连接数
        config.setMinIdle(MIN_IDLE);
        //表示当pool中的jedis实例都被分配完时，是否要进行阻塞
        config.setBlockWhenExhausted(true);
        //当blockWhenExhausted为true时，最大的阻塞时长
        config.setMaxWaitMillis(MAX_WAIT_MILLIS);
        //在创建Jedis实例时，测试连接可用性，默认关闭，如果打开，则保证创建的都是连接可用的Jedis实例；
        config.setTestOnCreate(true);
        //在资源池借出Jedis实例时，测试连接可用性，默认关闭，如果打开，则保证借出的都是可用的；
        config.setTestOnBorrow(true);
        //在Jedis归还Jedis资源池时，测试连接可用性，默认关闭；
        config.setTestOnReturn(true);

        //新建 jedis 连接池
        if(StringUtil.isNullOrEmpty(PASSWORD)){
            return new JedisPool(config, HOST, PORT, TIMEOUT);
        }else {
            return new JedisPool(config, HOST, PORT, TIMEOUT, PASSWORD);
        }
    }

}
