package com.rockyshen.core.register;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.rockyshen.core.config.RegistryConfig;
import com.rockyshen.core.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author rockyshen
 * @date 2024/11/8 21:39
 * 访问etcd数据库的类，主要担当注册中心
 */
public class EtcdRegistry implements Registry{
    // 根节点
    private static final String ETCD_ROOT_PATH = "/rpc/";
    // 所有方法都要用，提取为成员变量Field
    private Client client;
    private KV kvClient;

    // 存放已经注册到etcd中的key；用于遍历心跳监测
    private final Set<String> localRegistryNodeKeySet = new HashSet<>();

    // 服务发现的结果，存放到这个缓存中，直接从这里读取；
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    // 存放正在监听的key的Set集合
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        // etcd注册中心，一初始化，就开始心跳监测
        /**
         * 搞了半天，RpcApplication.init() 与 registry.init()导致client和kvClient为null的问题，核心是初始化顺序的问题
         * RpcApplication.init -> registry.init()和registry.heartBeat()应该解耦！
         */
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        Lease leaseClient = client.getLeaseClient();
        // 30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption);

        // 将注册到etcd注册中心的key加入这个set容器，用于心跳监测
        localRegistryNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey,StandardCharsets.UTF_8));

        // 将注册到etcd注册中心的key移除set容器，不再心跳监测
        localRegistryNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 服务发现，先从缓存中查一下
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if(cachedServiceMetaInfoList != null){
            return cachedServiceMetaInfoList;
        }

        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";    // 前缀搜索
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        try {
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();

            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(keyValue -> {
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                watch(key);  // 监视这个key，一旦被删除，就执行清除缓存
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
            // 写入缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;

        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    @Override
    public void destory() {
//        System.out.println("当前节点下线!");
        for(String key:localRegistryNodeKeySet){
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key+"节点下线失败！");
            }
        }

        if(kvClient != null){
            kvClient.close();
        }

        if(client != null){
            client.close();
        }
    }

    // 心跳监测
    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for(String key:localRegistryNodeKeySet){
                    try {
                        ByteSequence byteKey = ByteSequence.from(key, StandardCharsets.UTF_8);
                        List<KeyValue> keyValues = kvClient.get(byteKey).get().getKvs();

                        // 这个节点依据过期了，里面没有信息，被删掉了
                        if(CollUtil.isEmpty(keyValues)){
                            continue;
                        }

                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        // 续签，就是重新用这个serviceMetaInfo，注册一下
                        // TODO 用keepAlive()是不是也可以？
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        CronUtil.setMatchSecond(true);   // 设置秒匹配兼容性
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        // 加入set成功，之前没有监视过
        if(newWatch){
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8),watchResponse -> {
                List<WatchEvent> events = watchResponse.getEvents();
                for(WatchEvent event : events){
                    switch (event.getEventType()){
                        case DELETE:
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }

}
