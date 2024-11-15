package com.rockyshen.core.server.tcp;

import com.google.common.annotations.VisibleForTesting;
import com.rockyshen.core.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;

/**
 * @author rockyshen
 * @date 2024/11/14 11:22
 * 改为基于TCP协议的服务器，实现HttpServer接口
 */
public class VertxTcpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        // 这个就是TCP服务器啦！牛逼
        NetServer server = vertx.createNetServer();

        // 服务器处理tcpServerHandler的请求
        server.connectHandler(new TcpServerHandler());

        server.listen(port, result -> {
            // 监听端口，是否成功
            if(result.succeeded()){
                System.out.println("TCP server started on port " + port);
            }else {
                System.out.println("Failed to start TCP Server: " + result.cause());
            }
        });
    }

    private byte[] handleRequest(byte[] requestData){
//        System.out.println(requestData.toString());
        return "Hello,Client".getBytes();
    }

    // 测试server
    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
