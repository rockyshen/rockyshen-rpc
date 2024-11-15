package com.rockyshen.core.loadbalancer;

import com.rockyshen.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * @author rockyshen
 * @date 2024/11/15 17:37
 * 定义负载均衡的规范
 */
public interface LoadBalancer {

    ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
