package com.rockyshen.core.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author rockyshen
 * @date 2024/11/8 22:00
 * 注册中心，服务元信息
 */
@Data
public class ServiceMetaInfo {
    private String serviceName;

    private String serviceVersion = "1.0";

    private String serviceHost;

    private Integer servicePort;

    // TODO 服务分组，暂未实现
    private String serviceGroup = "default";

    public String getServiceKey() {
        return String.format("%s:%s",serviceName,serviceVersion);   // userService:1.0
    }

    /**
     * getServiceNodeKey是一个利用Field属性，拼接字段的方法
     * 返回：拼接完的节点key
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s",getServiceKey(),serviceHost,servicePort);  // userService:1.0/192.168.50.1:8083
    }

    public String getServiceAddress() {
        // 如果不包含http，例如localhost,返回出去的要补上，不然不能直接用
        if (!StrUtil.contains(serviceHost,"http")){
            return String.format("http://%s:%s",serviceHost,servicePort);
        }
        return String.format("%s:%s",serviceHost,servicePort);
    }
}
