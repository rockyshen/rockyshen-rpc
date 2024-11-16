package com.rockyshen.core.bootstrap;

import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.config.RegistryConfig;
import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.model.ServiceMetaInfo;
import com.rockyshen.core.model.ServiceRegisterInfo;
import com.rockyshen.core.register.LocalRegister;
import com.rockyshen.core.register.Registry;
import com.rockyshen.core.register.RegistryFactory;
import com.rockyshen.core.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * @author rockyshen
 * @date 2024/11/16 14:34
 * 服务提供者的启动类：1、将服务实现类注册到注册中心；2、启动web服务器
 * provider只要定义好serviceRegisterInfoList，然后ProviderBootstrap.init()即可
 */
public class ProviderBootstrap {
    // Rpc的全局配置

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        for(ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList){
            // 本地注册器  接口名 = 实现类
            String serviceName = serviceRegisterInfo.getServiceName();
            // 1、将接口名 = 实现类 加入LocalRegister
            LocalRegister.register(serviceName, serviceRegisterInfo.getImplClass());
            // 加入注册中心
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
        }

        // V2 基于TCP，启动TCP服务端
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
