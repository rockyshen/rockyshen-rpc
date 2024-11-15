package com.rockyshen.core.protocal;

import com.rockyshen.core.serializer.Serializer;
import com.rockyshen.core.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * @author rockyshen
 * @date 2024/11/14 11:53
 * 编码器
 * 将Java对象  -->  字节
 */
public class ProtocolMessageEncoder {

    public static Buffer encode(ProtocolMessage protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null){
            return Buffer.buffer();
        }

        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 向缓冲区依次写入字节，顺序严格遵守
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum == null){
            throw new RuntimeException("序列化协议不存在");
        }

        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
