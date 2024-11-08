package com.rockyshen.core;

import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rockyshen
 * @date 2024/11/5 13:19
 * 手写RPC框架-进阶版
 * Rpc的主启动:（没有main方法，就是让别人启动，自己提供服务）
 *  1、加载配置文件application.properties，映射到RpcConfig配置实体类上
 */
@Slf4j
public class RpcApplication {
    // TODO 因为要把host+port提供给ServiceProxy用，这里改为public对吗？
    // 后面看鱼皮是怎么使用这里的配置对象上的配置信息的
    public static volatile RpcConfig rpcConfig;     // 一个应用只有一个！

    /* 初始化，传入自定义配置
        1、从配置文件application.properties中读取配置信息，映射到RpcConfig上
        2、将rpcConfig这个映射完成的配置对象，传入doInit()中，真正执行初始化
     */
    public static void init(){
        RpcConfig newRpcConfig = null;
        try {
            // TODO prefix可以生成常量，这里没有传递env
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        } catch (Exception e) {
            newRpcConfig = new RpcConfig();  // RpcConfig中提供的默认值
        }
        doInit(newRpcConfig);
    }

    //
    private static void doInit(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("core init, config = {}",newRpcConfig.toString());
    }

    /* 双检索单例模式：确保多线程环境下，也只创建一个RpcConfig配置对象
        确保第一次创建时，多线程下时安全的
        之后都不用走到同步代码块，因为已经有了rpcConfig对象了！
     */
    public static RpcConfig getRpcConfig(){
        if(rpcConfig == null){
            synchronized (RpcApplication.class){
                if(rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
