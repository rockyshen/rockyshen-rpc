package com.rockyshen.provider;

import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.register.LocalRegister;
import com.rockyshen.core.server.HttpServer;
import com.rockyshen.core.server.VertxHttpServer;
import com.rockyshen.provider.impl.UserServiceImpl;
import com.rockyshen.service.UserService;

/**
 * @author rockyshen
 * @date 2024/11/5 16:25
 * 1、启动RpcApplication，读取配置信息（RpcApplication在谁那边运行，就读谁的配置信息）
 * 2、provider模块，告诉RPC，你针对哪个接口，提供了什么实现类
 * 3、启动web服务器
 * 4、
 */
public class CoreProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();

        // 1、将接口名 = 实现类 加入LocalRegister
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务器
        HttpServer httpServer = new VertxHttpServer();

        // 端口从RpcConfig对象上动态取！
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
