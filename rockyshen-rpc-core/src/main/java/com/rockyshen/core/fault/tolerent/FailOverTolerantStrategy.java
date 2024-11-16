package com.rockyshen.core.fault.tolerent;

import com.rockyshen.core.model.RpcResponse;

import java.util.Map;

/**
 * @author rockyshen
 * @date 2024/11/16 12:16
 * Fail-Over 故障转移
 */
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // TODO 获取其他服务节点的调用
        return null;
    }
}
