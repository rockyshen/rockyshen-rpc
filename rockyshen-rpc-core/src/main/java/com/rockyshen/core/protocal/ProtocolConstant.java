package com.rockyshen.core.protocal;

/**
 * @author rockyshen
 * @date 2024/11/14 10:54
 */
public interface ProtocolConstant {
    // 消息头固定占坑位 17bytes
    int MESSAGE_HEADER_LENGTH = 17;

    // 协议魔数
    byte PROTOCOL_MAGIC = 0x1;    // 十六进制的数1，对应十进制的1

    // 协议版本号
    byte PROTOCOL_VERSION = 0x1;
}
