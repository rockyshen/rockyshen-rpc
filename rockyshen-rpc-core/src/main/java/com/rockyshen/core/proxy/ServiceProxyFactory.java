package com.rockyshen.core.proxy;

import com.rockyshen.core.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * @author rockyshen
 * @date 2024/11/2 22:27
 * 工厂模式，创建代理类的实例
 * 提供一个服务类的类模版，返回这个类（代理后的）实例对象
 */
public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceClass){
        // 如果配置对象中开启了mock，就需要返回mock代理类
        if(RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }

        // 如果mock开启，就不返回userServiceProxy对象了，拦截掉了
        Object o = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ServiceProxy());
        return (T) o;
    }

    public static <T> T getMockProxy(Class<T> serviceClass){
        Object o = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new MockServiceProxy());
        return (T)o;
    }
}
