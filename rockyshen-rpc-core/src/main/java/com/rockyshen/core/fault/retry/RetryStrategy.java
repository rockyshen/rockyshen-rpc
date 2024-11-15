package com.rockyshen.core.fault.retry;

import com.rockyshen.core.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author rockyshen
 * @date 2024/11/15 23:28
 * 重试机制的接口
 */
public interface RetryStrategy {

    // 这里用Callable包裹RpcResponse，学习了
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
