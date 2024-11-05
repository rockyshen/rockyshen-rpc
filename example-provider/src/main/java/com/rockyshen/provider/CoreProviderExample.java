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
 */
public class CoreProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();

        // 将接口名 = 实现类 加入LocalRegister
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        // 提供服务
        HttpServer httpServer = new VertxHttpServer();

        // 端口从RpcConfig对象上动态取！
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
