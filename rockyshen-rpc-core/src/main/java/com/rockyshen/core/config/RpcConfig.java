package com.rockyshen.core.config;

import lombok.Data;

/**
 * @author rockyshen
 * @date 2024/11/5 12:42
 * RPC框架配置信息的实体类
 * 利用ytils/ConfigUtils工具类将接收到的配置信息，映射到本类上
 */
@Data
public class RpcConfig {
    private String name = "rockyshen-core";

    private String version = "1.0";

    private String serverHost = "localhost";

    private Integer serverPort = 8080;

    // 模拟数据是否开启，默认不开启
    private Boolean mock = false;

    // 配置文件中指定序列化器的key，默认JDK序列化器
    private String serializer = "jdk";

    public boolean isMock(){
        if(mock){
            return true;
        }
        return false;
    }
}
