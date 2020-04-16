package com.example.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.cache.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Resource
    private JedisPool jedisPool;

    /**
     * 获取jedis实例
     */
    public Jedis getResource() {
        return jedisPool.getResource();
    }

    /**
     * 设置key与value
     */
    public void set(String key, String value, String nxxx, String expx, long time) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            SetParams setParams = new SetParams();
            if ("nx".equals(nxxx)) {
                setParams.nx();
            } else {
                setParams.xx();
            }
            if ("ex".equals(expx)) {
                setParams.ex((int) time);
            } else {
                setParams.px(time);
            }
            jedis.set(key, value, setParams);
        } catch (Exception e) {
            log.error("Redis set error: {} - {} , value: {}", e.getMessage(), key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 根据key获取value
     */
    public String get(String key) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("Redis set error: {} - {} ", e.getMessage(), key);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断是否存在key
     */
    public boolean containKey(String key) {
        boolean b;
        Jedis jedis = null;
        try {
            jedis = getResource();
            b = jedis.exists(key);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis server error:：{}", e.getMessage());
            return false;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 释放jedis实例资源
     */
    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 获取key
     */
    public String getKeyForAop(JoinPoint joinPoint, HttpServletRequest request) {

        //获取参数的序列化
        Object[] objects = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object object : objects) {
            sb.append(JSON.toJSONString(object));
        }

        String args = DigestUtils.md5DigestAsHex(sb.toString().getBytes());

        //获取请求url
        String url = request.getRequestURI();
        //获取请求的方法
        String method = request.getMethod();
        //获取当前日期,规避默认init情况
        String date = LocalDate.now().toString();
        //key值获取
        return args + url + method + date;
    }
}
