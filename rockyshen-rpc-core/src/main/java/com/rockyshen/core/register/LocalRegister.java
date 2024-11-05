package com.rockyshen.core.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rockyshen
 * @date 2024/10/30 17:12
 * 本地服务注册器
 * 目的：服务名称 = 服务实现类
 * 例如我只要输入common模版的UserService这个服务，本类就要帮我找到Provider模版的UserServiceImpl这个实现类
 */
public class LocalRegister {
    // 只有一个map,一旦生成就不能变
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    public static void register(String serviceName,Class<?> implClass){
        map.put(serviceName,implClass);
    }

    public static Class<?> get(String serviceName){
        return map.get(serviceName);
    }

    public static void remove(String serviceName){
        map.remove(serviceName);
    }
}
