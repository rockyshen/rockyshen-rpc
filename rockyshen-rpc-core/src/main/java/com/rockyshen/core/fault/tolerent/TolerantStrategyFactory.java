package com.rockyshen.core.fault.tolerent;

import com.rockyshen.core.fault.retry.NoRetryStrategy;
import com.rockyshen.core.fault.retry.RetryStrategy;
import com.rockyshen.core.spi.SpiLoader;

/**
 * @author rockyshen
 * @date 2024/11/16 12:18
 */
public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);   // SPI机制，加载接口！
    }

    public static TolerantStrategy getInstance(String key){
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }

    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();
}
