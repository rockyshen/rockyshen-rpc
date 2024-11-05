package com.rockyshen.core.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * @author rockyshen
 * @date 2024/11/5 12:43
 * 从setting文件中读取配置信息
 * key-value映射给RpcConfig配置实体对象
 * 会在RpcApplication启动时调用本方法
 */
public class ConfigUtils {

    public static <T> T loadConfig(Class<T> clazz, String prefix, String env){
        StringBuilder configFile = new StringBuilder("application");

        if(StrUtil.isNotBlank(env)){
            configFile.append("-").append(env);
        }
        configFile.append(".properties");

        Props props = new Props(configFile.toString());
        T bean = props.toBean(clazz, prefix);
        return bean;
    }

    public static <T> T loadConfig(Class<T> clazz, String prefix){
        return loadConfig(clazz,prefix,"");
    }
}
