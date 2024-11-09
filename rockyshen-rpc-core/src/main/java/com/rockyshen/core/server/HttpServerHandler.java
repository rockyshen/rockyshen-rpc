package com.rockyshen.core.server;


import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.model.RpcRequest;
import com.rockyshen.core.model.RpcResponse;
import com.rockyshen.core.register.LocalRegister;
import com.rockyshen.core.serializer.JDKSerializer;
import com.rockyshen.core.serializer.Serializer;
import com.rockyshen.core.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author rockyshen
 * @date 2024/11/1 13:22
 * 接收web服务器的请求，并响应结果
 * 1、反序列化请求为RpcRequest对象，获取：1服务名、2方法名、3调用参数的数据类型List、4实际参数List
 * 2、从LocalRegister的Map中，基于 1服务名，查到具体实现类
 * 3、通过反射方式：调用实现类的 2方法名，得到结果
 * 4、对结果进行封装，序列化，写入响应中；
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    @Override
    public void handle(HttpServerRequest request) {
//        Serializer serializer = new JDKSerializer();
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                // TODO hessian反序列化后，这里报错！
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            RpcResponse rpcResponse = new RpcResponse();

            // 特殊情况的判断
            if(rpcRequest == null){
                rpcResponse.setDescription("rpcRequest is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }

            Method method = null;
            try {
                // 去LocalRegister中查，获取到服务实现类的类模版implClass
                Class<?> implClass = LocalRegister.get(rpcRequest.getServiceName());
                method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                // 这里要类型转换成方法的返回类型
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setDescription("成功");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setException(e);
                rpcResponse.setDescription(e.getMessage());
            }

            doResponse(request,rpcResponse,serializer);
        });
    }

    // 执行响应的逻辑
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type","application/json");

        try {
            byte[] serialized = serializer.serialize(rpcResponse);    // 将响应对象rpcResponse序列化为字节数组
            httpServerResponse.end(Buffer.buffer(serialized));       // 把字节数组放入响应体中
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
