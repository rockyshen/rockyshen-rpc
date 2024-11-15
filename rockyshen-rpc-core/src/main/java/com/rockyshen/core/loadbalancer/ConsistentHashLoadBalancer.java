package com.rockyshen.core.loadbalancer;

import com.rockyshen.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author rockyshen
 * @date 2024/11/15 17:49
 * 基于一致性hash的负载均衡算法
 * 请求参数进行hash计算，去一致性hash环上找最近的一个节点
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{

    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();

    // 固定有100个节点
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        // 如果为空
        if(serviceMetaInfoList == null){
            return null;
        }

        for(ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList){
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++){
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "+" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        int hash = getHash(requestParams);

        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry == null){   // 表示传入的hash已经是最大了，那就绕到头上去
            entry = virtualNodes.firstEntry();
        }

        return entry.getValue();
    }

    private int getHash(Object key){
        return key.hashCode();
    }
}
