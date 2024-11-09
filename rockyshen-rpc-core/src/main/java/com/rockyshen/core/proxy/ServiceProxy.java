package com.rockyshen.core.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.model.RpcRequest;
import com.rockyshen.core.model.RpcResponse;
import com.rockyshen.core.model.ServiceMetaInfo;
import com.rockyshen.core.register.Registry;
import com.rockyshen.core.register.RegistryFactory;
import com.rockyshen.core.serializer.JDKSerializer;
import com.rockyshen.core.serializer.Serializer;
import com.rockyshen.core.serializer.SerializerFactory;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author rockyshen
 * @date 2024/11/2 22:08
 * consumer模块告诉我，要调用哪个接口，哪个方法
 * 利用动态代理，包装一个可发请求的代理类
 * 利用实现InvocationHandler接口，实现JDK动态代理
 */
public class ServiceProxy implements InvocationHandler {

    final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Serializer serializer = new JDKSerializer();
        String serviceName = method.getDeclaringClass().getName();
        // 建造者模式
        // 反射的运用
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            byte[] serialized = serializer.serialize(rpcRequest);
            // 完成：这里Provider提供者的vertx服务器启动路径写死了，后续要优化！  -> 从配置对象RpcConfig上动态读取
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion("1.0");
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfos)){
                throw new RuntimeException("暂无服务地址");
            }
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfos.get(0);
            // 从注册中心中选到的服务地址
            HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress()).body(serialized).execute();
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
