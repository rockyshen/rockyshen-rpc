package com.rockyshen.core.register;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.rockyshen.core.config.RegistryConfig;
import com.rockyshen.core.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author rockyshen
 * @date 2024/11/12 22:49
 * 基于Zookeeper实现的注册中心
 */
@Slf4j
public class ZookeeperRegistry implements Registry{
    private CuratorFramework client;    // zk客户端
    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;   // 基于zk客户端，生成的“服务发现器”

    private static final String ZK_ROOT_PATH = "/rpc/zk";

    // 存放已经注册到etcd中的key；用于遍历心跳监测
    private final Set<String> localRegistryNodeKeySet = new HashSet<>();

    // 服务发现的结果，存放到这个缓存中，直接从这里读取；
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    // 存放正在监听的key的Set集合
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();

        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();

        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        String registryKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegistryNodeKeySet.add(registryKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String registryKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegistryNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if(cachedServiceMetaInfoList != null){
            return cachedServiceMetaInfoList;
        }

        try {
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery.queryForInstances(serviceKey);

            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream().map(serviceInstance -> {
                ServiceMetaInfo payload = serviceInstance.getPayload();
                return payload;
            }).collect(Collectors.toList());

            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    // 主动下线，需要手动清理localRegistryNodeKeySet
    @Override
    public void destory() {
        log.info("当前节点下线");
        localRegistryNodeKeySet.forEach(key -> {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败" + e);
            }
        });

        if(client != null){
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // 因为创建的是临时节点，client一旦与zk svr断连，节点自动消失
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKeySet.add(watchKey);   // set存放唯一元素，如果已经有了，返回false

        // 这个key之前没有监视过
        if(newWatch){
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener.builder()
                            .forDeletes(childData -> registryServiceCache.clearCache())
                            .forChanges(((oldNode,node) -> registryServiceCache.clearCache()))
                            .build()

            );
        }
    }

    /**
     * 由于ServiceDiscovery服务发现器的registry和unRegistry的参数必须是ServiceInstance<T>
     * 所以利用本方法，将其他类型，转为ServiceInstance类型
     * @param serviceMetaInfo
     * @return
     */
    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo){
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();

        try {
            ServiceInstance<ServiceMetaInfo> build = ServiceInstance.<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
            return build;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
