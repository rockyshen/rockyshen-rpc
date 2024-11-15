package com.rockyshen.core.server.tcp;

import com.rockyshen.core.model.RpcRequest;
import com.rockyshen.core.model.RpcResponse;
import com.rockyshen.core.protocal.ProtocolMessage;
import com.rockyshen.core.protocal.ProtocolMessageDecoder;
import com.rockyshen.core.protocal.ProtocolMessageEncoder;
import com.rockyshen.core.protocal.ProtocolMessageTypeEnum;
import com.rockyshen.core.register.LocalRegister;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author rockyshen
 * @date 2024/11/14 13:31
 * 处理从客户端投递过来的请求数据
 * 接收：Handler类型的NetSocket
 * 由于逻辑比较复杂，所以独立成一个类
 */
public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        // 接收到客户端的buffer，然后解包，校验header，获取到body，反射调用，获得rpcResponse
        // 将rpcResponse封装进ProtocolMessage返回
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(requestBuffer -> {
            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>)ProtocolMessageDecoder.decode(requestBuffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            RpcResponse rpcResponse = new RpcResponse();
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

            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte)ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                // 编码后，响应返回 ==> responseBuffer
                Buffer responseBuffer = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(responseBuffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
        netSocket.handler(bufferHandlerWrapper);
    }
}
