package com.example.cache.util;

import com.alibaba.fastjson.JSON;
import com.example.cache.service.CacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class CacheAspect {

    @Resource
    private CacheService cacheService;

    /**
     * 设置切入点
     */
    @Pointcut("@annotation(com.example.cache.util.NeedCacheAop)")
    public void annotationAspect() {
    }

    @Pointcut("execution(public * com.example.cache.service.impl.UserService*.*(..))")
    public void controllerAspect() {
    }

    /**
     * 环绕通知
     */
    @Around(value = "controllerAspect()||annotationAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws ClassNotFoundException {

        //获取请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        //存储接口返回值
        Object object = new Object();

        //获取注解对应配置过期时间
        NeedCacheAop cacheAop = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NeedCacheAop.class);  //获取注解自身
        String nxxx;
        String expx;
        long time;
        if (cacheAop == null) {//规避使用第二种切点进行缓存操作的情况
            nxxx = "nx";
            expx = "ex";
            time = 30;  //默认过期时间为30分钟
        } else {
            nxxx = cacheAop.nxxx();
            expx = cacheAop.expx();
            time = cacheAop.time();
        }
        //获取key
        String key = cacheService.getKeyForAop(joinPoint, request);
        String classKey = key + "/class";
        if (cacheService.containKey(key)) {
            String jsonString = cacheService.get(key);
            String className = cacheService.get(classKey);

            if ("fail".endsWith(jsonString)) {  //规避redis服务不可用
                try {
                    //执行接口调用的方法
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {

                object = className == null ? null : JSON.parseObject(jsonString, Class.forName(className));
            }
        } else {
            try {
                //执行接口调用的方法并获取返回值
                object = joinPoint.proceed();
                String className = object == null ? null : object.getClass().getName();
                String jsonString = JSON.toJSONString(object);
                cacheService.set(key, jsonString, nxxx, expx, time);
                if (className != null) {
                    cacheService.set(classKey, className, nxxx, expx, time);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return object;
    }
}