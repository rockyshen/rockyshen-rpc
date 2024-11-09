package com.rockyshen.core.config;

import lombok.Data;

/**
 * @author rockyshen
 * @date 2024/11/8 22:06
 * 注册中心的配置
 */
@Data
public class RegistryConfig {
    private String registry = "etcd";

    // 运行etcd服务的地址
    private String address = "http://localhost:2379";

    private String username;

    private String password;

    // 连接注册中心的超时时间
    private Long timeout = 10000L;
}
