package com.rockyshen.provider;

import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.config.RegistryConfig;
import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.model.ServiceMetaInfo;
import com.rockyshen.core.register.LocalRegister;
import com.rockyshen.core.register.Registry;
import com.rockyshen.core.register.RegistryFactory;
import com.rockyshen.core.server.HttpServer;
import com.rockyshen.core.server.VertxHttpServer;
import com.rockyshen.core.server.tcp.VertxTcpServer;
import com.rockyshen.provider.impl.UserServiceImpl;
import com.rockyshen.service.UserService;

/**
 * @author rockyshen
 * @date 2024/11/5 16:25
 * 1、启动RpcApplication，读取配置信息（RpcApplication在谁那边运行，就读谁的配置信息）
 * 2、provider模块，告诉RPC，你针对哪个接口，提供了什么实现类
 *    2-1  加入etcd注册中心
 * 3、启动web服务器
 * 4、
 */
public class CoreProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();

        // 本地注册器  接口名 = 实现类
        String serviceName = UserService.class.getName();
        // 1、将接口名 = 实现类 加入LocalRegister
        LocalRegister.register(serviceName, UserServiceImpl.class);

        // 加入注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        /** provider端，如果显式调用registry.init的话，就会覆盖RpcApplication.init的
         * 重新调用的话，RegistryFactory会在第一次init时生成的对象，存在instanceCache这个Map中；
         * 第二次直接取，对象是同一个！
         */
        registry.init(registryConfig);
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

        try {
            // 由于register里的client和KVClient都是null，所以这里报空指针异常
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // V1 基于Http启动web服务器
//        HttpServer httpServer = new VertxHttpServer();
        // 端口从RpcConfig对象上动态取！
//        httpServer.doStart(rpcConfig.getServerPort());

        // V2 基于TCP，启动TCP服务端
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
