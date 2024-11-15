package com.rockyshen.core.protocal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rockyshen
 * @date 2024/11/14 10:47
 * 之前在ServiceProxy代理类中，是将服务封装成了HTTP进行传输
 * 自此，自定义RPC协议，替代HTTP传输，更加轻量、更加高效
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    private Header header;

    private T body;

    /** 静态内部类
     * 声明为public，因为后面需要基于ProtocolMessage获取Header对象
     */
    @Data
    public static class Header {
        // 魔数，保证安全性，caffeebaby  固定：8bit = 1byte
        private byte magic;

        // 本协议的版本号，类似 HTTP/1.1   固定：8bit = 1byte
        private byte version;

        /** 序列化器    固定：8bit = 1byte
         * ProtocolMessageSerializerEnum
         */
        private byte serializer;

        /** 消息类型（请求/响应）   固定：8bit = 1byte
         * ProtocolMessageTypeEnum
         * 0:请求   1:响应    2:心跳    3:其他
         */
        private byte type;

        /** 状态，例如响应状态：200   固定：8bit = 1byte
         * ProtocolMessageStatusEnum
         * 20:成功   40:bad request   50:bad response
         */
        private byte status;

        // 请求ID     固定：long数据类型占64bit = 8byte
        private long requestId;

        // 消息体长度    固定：int数据类型占32bit = 4byte
        private int bodyLength;
    }
}
