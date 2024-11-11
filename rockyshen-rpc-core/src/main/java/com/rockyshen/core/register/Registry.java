package com.rockyshen.core.register;

import com.rockyshen.core.config.RegistryConfig;
import com.rockyshen.core.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author rockyshen
 * @date 2024/11/8 22:08
 * 注册中心的接口，定义了注册中心应该具备的方法，EtcdRegistry就是对它的实现
 */
public interface Registry {
    // 传入配置对象，初始化一个注册中心
    void init(RegistryConfig registryConfig);

    // 传递服务元信息，向etcd注册中心进行注册
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    // 注销
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    // 服务发现，根据传递的服务key，发现所有提供服务的节点
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    // 销毁注册中心
    void destory();

    // 心跳监测
    void heartBeat();

}
