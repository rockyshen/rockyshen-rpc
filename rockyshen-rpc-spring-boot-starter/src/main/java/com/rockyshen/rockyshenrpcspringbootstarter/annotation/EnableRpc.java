package com.rockyshen.rockyshenrpcspringbootstarter.annotation;

import com.rockyshen.rockyshenrpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.rockyshen.rockyshenrpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.rockyshen.rockyshenrpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rockyshen
 * @date 2024/11/16 15:10
 * 表示要引入rockyshen-rpc，并执行初始化方法
 * 注意：provider和consumer的初始化方法不同，需要区分
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RpcInitBootstrap.class, RpcConsumerBootstrap.class, RpcProviderBootstrap.class})   // 将多个配置类，汇总到一个中
public @interface EnableRpc {
    boolean needServer() default true;  // needServer表示是否需要启动web服务器，provider需要，consumer不需要
}
