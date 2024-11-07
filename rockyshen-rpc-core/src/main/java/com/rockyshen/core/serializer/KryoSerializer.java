package com.rockyshen.core.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author rockyshen
 * @date 2024/11/7 10:17
 * 基于Kyrp框架，实现序列化与反序列化器
 */
public class KryoSerializer implements Serializer{
    // kryo线程不安全，放到ThreadLocal中，保证一个线程只有一个kyro
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);   // 关闭动态序列化与反序列化类，存在安全问题
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);   // output是处理流吗？ ByteArrayOutputStream是基本节点流
        KRYO_THREAD_LOCAL.get().writeObject(output,object);
        output.close();
        return byteArrayOutputStream.toByteArray();
        // TODO ThreadLocal不remove吗？
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T result = KRYO_THREAD_LOCAL.get().readObject(input, type);
        input.close();
        return result;

        // TODO ThreadLocal不remove吗？
    }
}
