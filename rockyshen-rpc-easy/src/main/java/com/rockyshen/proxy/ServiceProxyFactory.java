package com.rockyshen.proxy;

import java.lang.reflect.Proxy;

/**
 * @author rockyshen
 * @date 2024/11/2 22:27
 * 工厂模式，创建代理类的实例
 * 提供一个服务类的类模版，返回这个类的实例对象
 */
public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceClass){
        Object o = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ServiceProxy());
        return (T) o;
    }
}
