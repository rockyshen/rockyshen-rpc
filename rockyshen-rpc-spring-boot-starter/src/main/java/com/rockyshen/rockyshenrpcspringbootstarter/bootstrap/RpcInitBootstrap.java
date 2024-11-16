package com.rockyshen.rockyshenrpcspringbootstarter.bootstrap;

import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.server.tcp.VertxTcpClient;
import com.rockyshen.core.server.tcp.VertxTcpServer;
import com.rockyshen.rockyshenrpcspringbootstarter.annotation.EnableRpc;
import com.rockyshen.rockyshenrpcspringbootstarter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Service;

/**
 * @author rockyshen
 * @date 2024/11/16 15:21
 * 全局启动类，Spring项目启动时，获取到@EnableRpc注解，就执行本方法
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar{
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        boolean needServer = (boolean)importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        if(needServer){
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        }else{
            log.info("是消费端启动，不需要启动Web服务器");
        }
    }
}
