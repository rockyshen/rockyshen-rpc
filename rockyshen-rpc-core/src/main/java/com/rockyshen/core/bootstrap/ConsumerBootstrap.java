package com.rockyshen.core.bootstrap;

import com.rockyshen.core.RpcApplication;

/**
 * @author rockyshen
 * @date 2024/11/16 14:34
 * 服务消费者的启动类：只需要RpcApplication.init()即可
 */
public class ConsumerBootstrap {
    public static void init(){
        RpcApplication.init();
    }
}
