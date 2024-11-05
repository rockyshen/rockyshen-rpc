package com.rockyshen.core.server;

/**
 * @author rockyshen
 * @date 2024/10/30 16:59
 * HTTP服务接口
 * 提供一种开放性：以后你可以换成其他web服务器，只要重写HttpServer接口即可！
 */
public interface HttpServer {
    void doStart(int port);
}
