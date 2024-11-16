package com.rockyshen.core.fault.tolerent;

import com.rockyshen.core.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author rockyshen
 * @date 2024/11/16 11:41
 * Fail-Safe 静默处理，发送了错误，不吱声，就当什么也没发生（log记录一下），返回一个正常对象
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默异常处理",e);
        return new RpcResponse();
    }
}
