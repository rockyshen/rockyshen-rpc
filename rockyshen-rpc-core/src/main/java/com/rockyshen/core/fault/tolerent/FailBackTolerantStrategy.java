package com.rockyshen.core.fault.tolerent;

import com.rockyshen.core.model.RpcResponse;

import java.util.Map;

/**
 * @author rockyshen
 * @date 2024/11/16 11:43
 * Fail-Back 发生错误时，调用其他降级的服务
 */
public class FailBackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // TODO 获取降级的服务调用
        return null;
    }
}
