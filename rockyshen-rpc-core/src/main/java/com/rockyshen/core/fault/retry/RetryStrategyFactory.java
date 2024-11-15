package com.rockyshen.core.fault.retry;

import com.rockyshen.core.register.EtcdRegistry;
import com.rockyshen.core.register.Registry;
import com.rockyshen.core.spi.SpiLoader;

/**
 * @author rockyshen
 * @date 2024/11/15 23:58
 */
public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);   // SPI机制，加载接口！
    }

    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();
}
