package com.rockyshen.core.loadbalancer;

import com.rockyshen.core.register.EtcdRegistry;
import com.rockyshen.core.register.Registry;
import com.rockyshen.core.spi.SpiLoader;

/**
 * @author rockyshen
 * @date 2024/11/15 17:57
 * 基于SPI机制，生成对应负载均衡器实例
 */
public class LoadBalancerFactory {

    static {
        SpiLoader.load(Registry.class);   // SPI机制，加载接口！
    }

    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class, key);
    }

    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();
}
