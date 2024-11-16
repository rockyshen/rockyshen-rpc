package com.rockyshen.rockyshenrpcspringbootstarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rockyshen
 * @date 2024/11/16 15:15
 * 服务消费者注解，注入被代理过的对象
 * 类似于获取这玩意：UserService userService = ServiceProxyFactory.getProxy(UserService.class);
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {
    Class<?> interfaceClass() default void.class;

    String serviceVersion() default "1.0";

    String loadBalancer() default "roundRobin";

    String retryStrategy() default "no";

    String tolerantStrategy() default "failFast";

    boolean mock() default false;
}
