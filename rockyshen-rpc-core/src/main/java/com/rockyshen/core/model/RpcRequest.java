package com.rockyshen.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author rockyshen
 * @date 2024/11/1 13:03
 * 接收web服务器的请求的【实体类模型】
 * 1、依据请求参数，映射，服务和方法；利用反射调用方法
 * 封装反射所需要的信息：1服务名、2方法名、3调用参数的数据类型List、4实际参数List
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {
//    private static final long serialVersionUID = 1L;

    private String serviceName;

    private String methodName;

    // 服务版本
    private String serviceVersion = "1.0";

    private Class<?>[] parameterTypes;

    private Object[] args;
}
