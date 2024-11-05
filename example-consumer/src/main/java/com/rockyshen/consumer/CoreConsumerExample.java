package com.rockyshen.consumer;

import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.utils.ConfigUtils;

/**
 * @author rockyshen
 * @date 2024/11/5 16:11
 */
public class CoreConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
