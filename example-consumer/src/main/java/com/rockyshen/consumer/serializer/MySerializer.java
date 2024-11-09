package com.rockyshen.consumer.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.rockyshen.core.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author rockyshen
 * @date 2024/11/7 10:17
 *
 */
public class MySerializer implements Serializer {


    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return null;
    }
}
