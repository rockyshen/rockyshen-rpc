package com.rockyshen.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.rockyshen.model.RpcRequest;
import com.rockyshen.model.RpcResponse;
import com.rockyshen.serializer.JDKSerializer;
import com.rockyshen.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author rockyshen
 * @date 2024/11/2 22:08
 * 根据consumer提供的调用方法，利用动态代理，包装一个可发请求的代理类
 * 利用实现InvocationHandler接口，实现JDK动态代理
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = new JDKSerializer();
        // 建造者模式
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            byte[] serialized = serializer.serialize(rpcRequest);
            HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(serialized).execute();
            byte[] bytes = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(bytes, RpcResponse.class);
            Object result = rpcResponse.getData();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
