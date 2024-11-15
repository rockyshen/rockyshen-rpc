package com.rockyshen.core.loadbalancer;

import com.rockyshen.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author rockyshen
 * @date 2024/11/15 17:47
 * 基于随机的负载均衡算法：随机将分配给每一台服务器
 */
public class RandomLoadBalancer implements LoadBalancer{

    private final Random random = new Random();
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
        ServiceMetaInfo serviceMetaInfo = serviceMetaInfoList.get(random.nextInt(size));
        return serviceMetaInfo;
    }
}
