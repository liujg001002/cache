package com.example.cache.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class JedisConfiguration extends CachingConfigurerSupport {
    private Logger logger = LoggerFactory.getLogger(JedisConfiguration.class);
    @Value("${spring.jedis.port}")
    private Integer port;
    @Value("${spring.jedis.host}")
    private String host;
    @Value("${spring.jedis.max.total}")
    private Integer maxTotal;
    @Value("${spring.jedis.max.idle}")
    private Integer maxIdle;
    @Value("${spring.jedis.max.waitmillis}")
    private Long maxWaitMillis;
    @Value("${spring.jedis.password}")
    private String password;

    public JedisConfiguration() {
    }

    /**
     * 设置
     */
    @Bean
    public JedisPool getJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMaxTotal(maxTotal);
        if ("".equals(password)) {
            password = null;
        }
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, 1000, password);
        logger.info("JedisPool build success!");
        logger.info("Redis host：" + host + ":" + port);

        /*ExecutorService service = Executors.newCachedThreadPool();
        service.execute(()->{
            //创建监听
            Jedis jedis = jedisPool.getResource();
            jedis.psubscribe(new KeyExpiredListener(), "__key*__:*");
        });*/
        return jedisPool;
    }
}
