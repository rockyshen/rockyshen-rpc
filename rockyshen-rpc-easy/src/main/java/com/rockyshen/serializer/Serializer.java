package com.rockyshen.serializer;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;

/**
 * @author rockyshen
 * @date 2024/10/30 17:41
 * 序列化器接口：定义序列化、反序列化方法的规约，便于之后替换
 */
public interface Serializer {
    /**
     * 序列化
     * 传入某个类型的对象，序列化为byte型数组
     * @param object
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     * 传入一个byte型数组和一个类型，反序列化为这个类型的具体实例
     * @param bytes
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T>type) throws IOException;
}
