package com.rockyshen.core.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.model.RpcRequest;
import com.rockyshen.core.model.RpcResponse;
import com.rockyshen.core.model.ServiceMetaInfo;
import com.rockyshen.core.protocal.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author rockyshen
 * @date 2024/11/14 11:33
 * 测试接收VertxTcpServer的客户端
 */
public class VertxTcpClient {
    public void start(){
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888,"localhost",result -> {
            // 是否成功建立连接
            if(result.succeeded()){
                System.out.println("-----Connected to TCP server-----");

                NetSocket socket = result.result();
                // 连续发1000次请求
                for (int i = 0; i < 1000 ; i++){
                    socket.write("Hello, server!Hello, server!Hello, server!Hello, server!");
                }

                socket.handler(buffer -> {
                    System.out.println("Received response from TCP server: "+ buffer.toString());
                });
            }else{
                System.out.println("Failed to connect to TCP server");
            }
        });
    }
    // 测试client
    public static void main(String[] args) {
        new VertxTcpClient().start();
    }

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();   // 实例化一个发送TCP的客户端
        // Vertx提供的请求处理器是异步的。为了更好获取结果，利用CompletableFuture转异步为同步
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
            // 下面是客户端与服务端连上之后的处理逻辑
            if (!result.succeeded()) {
                System.out.println("Failed to connect to TCP Server");
                // 抛出异常，让重试机制生效！
                responseFuture.completeExceptionally(new RuntimeException("Failed to connect to TCP Server"));
                return;
            }
            NetSocket socket = result.result();
            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
            ProtocolMessage.Header header = new ProtocolMessage.Header();
            // 发送数据
            // 构造header
            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
            String serializerKey = RpcApplication.getRpcConfig().getSerializer();
            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(serializerKey).getKey());
            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
            header.setRequestId(IdUtil.getSnowflakeNextId());
            // 构造协议消息
            protocolMessage.setHeader(header);
            protocolMessage.setBody(rpcRequest);
            // 构造好了，进行编码
            try {
                Buffer encodedBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                socket.write(encodedBuffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }

            // 客户端handler：处理从服务端返回的响应数据
            // 装饰器，增加定长的解码器RecordParser，避免半包和沾包问题
            TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(responseBuffer -> {
                try {
                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(responseBuffer);
                    RpcResponse rpcResponse = rpcResponseProtocolMessage.getBody();
                    responseFuture.complete(rpcResponse);  // 异步返回的
                } catch (IOException e) {
                    throw new RuntimeException("协议消息解码错误");
                }
            });
            socket.handler(bufferHandlerWrapper);
        });

        RpcResponse rpcResponse = responseFuture.get();

        netClient.close();
        return rpcResponse;
    }
}