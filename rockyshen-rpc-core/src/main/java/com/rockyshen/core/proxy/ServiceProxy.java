package com.rockyshen.core.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.fault.retry.RetryStrategy;
import com.rockyshen.core.fault.retry.RetryStrategyFactory;
import com.rockyshen.core.model.RpcRequest;
import com.rockyshen.core.model.RpcResponse;
import com.rockyshen.core.model.ServiceMetaInfo;
import com.rockyshen.core.protocal.*;
import com.rockyshen.core.register.Registry;
import com.rockyshen.core.register.RegistryFactory;
import com.rockyshen.core.serializer.JDKSerializer;
import com.rockyshen.core.serializer.Serializer;
import com.rockyshen.core.serializer.SerializerFactory;
import com.rockyshen.core.server.tcp.VertxTcpClient;
import io.netty.util.concurrent.CompleteFuture;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author rockyshen
 * @date 2024/11/2 22:08
 * consumer模块告诉我，要调用哪个接口，哪个方法
 * 利用动态代理，包装一个可发请求的代理类
 * 利用实现InvocationHandler接口，实现JDK动态代理
 */
public class ServiceProxy implements InvocationHandler {

    // 调用代理
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从序列化器工厂获取一个序列化器对象！
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

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
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);   // 服务名，从你调用的对象反射得来！
            serviceMetaInfo.setServiceVersion("1.0");
            // 基于你构造的serviceMetaInfo对象，去注册中心中服务发现，可以用的IP地址！
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfos)){
                throw new RuntimeException("暂无服务地址");
            }
            // 服务发现，得到的serviceMetaInfo实例
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfos.get(0);

            // V1，基于HTTP发送代理服务,hutool的Http客户端
//            HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress()).body(serialized).execute();
//            byte[] bytes = httpResponse.bodyBytes();

            // V2，基于TCP、自定义的协议发送代理服务，抽取到VertxTcpClient中
            // 增加重试机制
            RetryStrategy retryStrategy =RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            RpcResponse rpcResponse = retryStrategy.doRetry( ()->
                VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
            return rpcResponse.getData();

        } catch (IOException e) {
            throw new RuntimeException("调用失败");
        }
    }
}
