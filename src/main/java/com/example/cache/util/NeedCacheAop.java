package com.example.cache.util;

import java.lang.annotation.*;

/**
 * 自定义注解，用于标识方法是否需要使用缓存
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedCacheAop {
    /**
     * 缓存策咯 nx:代表key不存在再进行缓存kv，xx:代表key存在再进行缓存kv  默认为不存在key缓存key即nx
     *
     * @return java.lang.String
     */
    String nxxx() default "nx";

    /**
     * 表过期时间单位 ex:秒 px:毫秒    默认为"秒"
     *
     * @return java.lang.String
     */
    String expx() default "ex";

    /**
     * 过期时间 秒
     *
     * @return java.lang.String
     */
    long time() default 30;
}