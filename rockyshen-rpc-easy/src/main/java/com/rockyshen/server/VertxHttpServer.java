package com.rockyshen.server;

import io.vertx.core.Vertx;

/**
 * @author rockyshen
 * @date 2024/10/30 17:00
 * 本类：单纯就是启动Vertx的服务器，没有任何web服务器的处理功能
 * web服务器的处理能力，写在HttpServerHandler类继承自 Handler<HttpServerRequest>
 */
public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

//        server.requestHandler(request -> {
//            // 处理请求
//            System.out.println("接收到请求："+request.method()+" "+request.uri());
//            // 发送响应
//            request.response().putHeader("content-type","text/plain").end("Hello from Vertx Http Server");
//        });

        // 使用我们手写的处理请求、响应的逻辑  -> HttpServerHandler
        server.requestHandler(new HttpServerHandler());

        server.listen(port,result -> {
            if(result.succeeded()){
                System.out.println("Vertx Http Server正在监听端口："+ port);
            }else{
                System.out.println("启动Vertx Http Server失败"+ result.cause());
            }
        });
    }
}
