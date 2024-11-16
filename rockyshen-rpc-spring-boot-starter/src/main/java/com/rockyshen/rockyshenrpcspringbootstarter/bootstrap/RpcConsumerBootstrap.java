package com.rockyshen.rockyshenrpcspringbootstarter.bootstrap;

import com.rockyshen.core.proxy.ServiceProxyFactory;
import com.rockyshen.rockyshenrpcspringbootstarter.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @author rockyshen
 * @date 2024/11/16 15:21
 * 获取所有包含@RpcReference的注解
 * 通过注解的属性、反射机制，动态生成代理对象并注入给添加了注解的Field
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();  // beanClass就是加载的类
        Field[] declaredFields = beanClass.getDeclaredFields();
        for(Field field:declaredFields){
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);  // 找到加了@RpcReference注解的那个Field
            if(rpcReference != null){
                Class<?> interfaceClass = rpcReference.interfaceClass();   // 服务接口类
                if(interfaceClass == void.class){
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean,proxyObject);   // 动态生成代理对象并注入给添加了注解的Field！
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("字段注入代理对象失败",e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
