package com.rockyshen.core.register;

import com.rockyshen.core.spi.SpiLoader;

/**
 * @author rockyshen
 * @date 2024/11/8 22:31
 * 注册中心工厂，根据配置信息的不同，实例化不同的注册中心实现：etcd、zookeeper等
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);   // SPI机制，加载接口！
    }

    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class, key);
    }

    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();
}
