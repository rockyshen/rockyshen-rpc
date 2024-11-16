package com.rockyshen.rockyshenrpcspringbootstarter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rockyshen
 * @date 2024/11/16 15:13
 * 服务提供者注解，服务接口类！
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    Class<?> interfaceClass() default void.class;
    String serviceVersion() default "1.0";
}
