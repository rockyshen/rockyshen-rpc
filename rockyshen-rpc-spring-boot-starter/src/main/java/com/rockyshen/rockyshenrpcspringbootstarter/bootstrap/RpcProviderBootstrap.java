package com.rockyshen.rockyshenrpcspringbootstarter.bootstrap;

import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.config.RegistryConfig;
import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.model.ServiceMetaInfo;
import com.rockyshen.core.register.LocalRegister;
import com.rockyshen.core.register.Registry;
import com.rockyshen.core.register.RegistryFactory;
import com.rockyshen.rockyshenrpcspringbootstarter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author rockyshen
 * @date 2024/11/16 15:21
 * 获取所有包含@RpcService的注解
 * 通过注解的属性、反射机制，获取要注册的服务信息，并完成服务注册
 */
public class RpcProviderBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();   // 这个beanClass就是加载的类
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);  // 找到加了@RpcService注解的那个类！
        if(rpcService != null){
            Class<?> interfaceClass = rpcService.interfaceClass();   // 从@rpcService注解上获得接口的clazz对象
            if(interfaceClass == void.class){
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 1.本地注册
            LocalRegister.register(serviceName,beanClass);   // beanClass就是接口实现类！

            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 2.注册中心注册
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败",e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean,beanName);
    }
}
