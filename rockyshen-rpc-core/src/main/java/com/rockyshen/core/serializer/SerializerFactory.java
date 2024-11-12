package com.rockyshen.core.serializer;

import com.rockyshen.core.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rockyshen
 * @date 2024/11/7 11:06
 * 序列化器工厂：根据指定的key,生成对应的序列化器
 */
public class SerializerFactory {
    // 每个序列化器用key - value存储
//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>();
//    static {
//        KEY_SERIALIZER_MAP.put("jdk",new JDKSerializer());
//        KEY_SERIALIZER_MAP.put("json",new JsonSerializer());
//        KEY_SERIALIZER_MAP.put("kryo",new KryoSerializer());
//        KEY_SERIALIZER_MAP.put("hessian",new HessianSerializer());
//    }

    /** 使用静态代码块，在工厂首次加载时，就会调用SpiLoader把META-INF中指定的所有实现类都加载好
     * 之后通过调用getInstance()获取指定实现类的对象！
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    // 根据接口，找到所有实现类，根据key，找到特定那个实现类！
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }

    //    public static Serializer getInstance(String key){
//        return KEY_SERIALIZER_MAP.getOrDefault(key,DEFAULT_SERIALIZER);
//    }

    // 默认是JDK序列化器
    private static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();

}
