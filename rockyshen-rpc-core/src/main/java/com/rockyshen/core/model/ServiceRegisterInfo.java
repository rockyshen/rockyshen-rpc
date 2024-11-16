package com.rockyshen.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rockyshen
 * @date 2024/11/16 14:36
 * 服务注册到服务中心时，需要提供的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRegisterInfo<T> {
    // 服务名称
    private String serviceName;

    // 实现类
    private Class<? extends T> implClass;
}
