package com.rockyshen.core.fault.retry;

import com.rockyshen.core.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author rockyshen
 * @date 2024/11/15 23:32
 * 不执行任何重试！
 */
public class NoRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
