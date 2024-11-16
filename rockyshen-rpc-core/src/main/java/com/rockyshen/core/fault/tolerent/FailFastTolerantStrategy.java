package com.rockyshen.core.fault.tolerent;

import com.rockyshen.core.model.RpcResponse;

import java.util.Map;

/**
 * @author rockyshen
 * @date 2024/11/16 11:39
 * Fail-Fast 快速失败，一出现错误，立刻爆出
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错",e);
    }
}
