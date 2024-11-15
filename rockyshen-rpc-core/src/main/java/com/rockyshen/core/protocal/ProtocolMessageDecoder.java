package com.rockyshen.core.protocal;

import com.rockyshen.core.model.RpcRequest;
import com.rockyshen.core.model.RpcResponse;
import com.rockyshen.core.serializer.Serializer;
import com.rockyshen.core.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import jdk.nashorn.internal.ir.RuntimeNode;

import java.io.IOException;

/**
 * @author rockyshen
 * @date 2024/11/14 11:54
 * 解码器
 * 将字节  -->  Java对象
 */
public class ProtocolMessageDecoder {
    public static ProtocolMessage decode(Buffer buffer) throws IOException {
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC){
            throw new RuntimeException("魔数非法");
        }

        // 把buffer中的字节，按顺序拆分到不同的部分中,构建：header 和 bodyBytes
        ProtocolMessage.Header header = new ProtocolMessage.Header();   // new一个空的头部
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        System.out.println(header);   // 看看：提取完的header长什么样
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        System.out.println(bodyBytes);  // 看看：提取完的body长什么样

        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum == null){
            throw new RuntimeException("序列化消息的协议不存在，枚举类里没定义");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if(typeEnum == null){
            throw new RuntimeException("序列化消息的类型不存在，不是请求、不是响应、也不是心跳");
        }
        switch (typeEnum){
            case REQUEST:
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage(header,rpcRequest);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage(header,rpcResponse);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }
}
