package com.rockyshen.core.fault.tolerent;

import com.rockyshen.core.model.RpcResponse;

import java.util.Map;

/**
 * @author rockyshen
 * @date 2024/11/16 11:38
 * 定义容错机制的接口规约
 */
public interface TolerantStrategy {
    RpcResponse doTolerant(Map<String,Object> context, Exception e);
}
