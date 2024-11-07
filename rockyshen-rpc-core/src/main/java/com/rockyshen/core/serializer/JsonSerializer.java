package com.rockyshen.core.serializer;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockyshen.core.model.RpcRequest;
import com.rockyshen.core.model.RpcResponse;

import java.io.IOException;

/**
 * @author rockyshen
 * @date 2024/11/7 09:56
 * 参考自：鱼皮手写一个JSON序列化器
 * 居然不用FastJson 或 Jackson，只用了Jackson中的ObjectMapper类
 */
public class JsonSerializer implements Serializer{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes,type);
        if (obj instanceof RpcRequest){
            return handleRequest((RpcRequest) obj,type);
        }

        if(obj instanceof RpcResponse){
            return handleResponse((RpcResponse) obj,type);
        }
        return null;
    }

    // 由于Object的原始对象会被擦除，导致反序列化时会被作为LinkedHashMap无法转换为原始对象
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();   // 形参
        Object[] args = rpcRequest.getArgs();     // 实参

        // 对形参（数据类型） 和 实参的类型  进行比较
        for(int i = 0; i < parameterTypes.length; i++){
            Class<?> clazz = parameterTypes[i];
            // 如果类型不同，重新处理一下类型
            if(!clazz.isAssignableFrom(args[i].getClass())){
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes,clazz);
            }
        }
        return type.cast(rpcRequest);
    }

    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes,rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }
}
