package com.rockyshen.provider;

import com.rockyshen.provider.impl.UserServiceImpl;
import com.rockyshen.register.LocalRegister;
import com.rockyshen.server.HttpServer;
import com.rockyshen.server.VertxHttpServer;
import com.rockyshen.service.UserService;

/**
 * @author rockyshen
 * @date 2024/10/30 00:13
 * 提供服务者，也就是实现UserService接口中规约方法的具体业务逻辑！
 */
public class EasyProvideExample {
    public static void main(String[] args) {
        // 将接口名 = 实现类 加入LocalRegister
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        // 提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);

    }
}