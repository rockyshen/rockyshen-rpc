package com.rockyshen.core.register;

import com.rockyshen.core.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author rockyshen
 * @date 2024/11/11 22:36
 * 1、consumer端，每次执行服务发现，将serviceMetaInfo存到缓存中，这样不用每次都去调用
 * 2、利用etcd特性，watch指定key，当发生变化时，自动触发服务发现！
 */
public class RegistryServiceCache {
    List<ServiceMetaInfo> serviceCache;

    void writeCache(List<ServiceMetaInfo> newServiceCache){
        this.serviceCache = newServiceCache;
    }

    List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }

    void clearCache(){
        this.serviceCache = null;
    }
}
