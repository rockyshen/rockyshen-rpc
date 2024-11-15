package com.rockyshen.core.loadbalancer;

import com.rockyshen.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rockyshen
 * @date 2024/11/15 17:40
 * 基于轮询的负载均衡算法：按循环顺序将分配给每一台服务器
 */
public class RoundRobinLoadBalancer implements LoadBalancer{
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        // 如果为空
        if(serviceMetaInfoList == null){
            return null;
        }

        int size = serviceMetaInfoList.size();
        // 如果只有一个
        if(size == 1){
            return serviceMetaInfoList.get(0);
        }

        int index = currentIndex.getAndIncrement() % size;    // 递增
        return serviceMetaInfoList.get(index);
    }
}
