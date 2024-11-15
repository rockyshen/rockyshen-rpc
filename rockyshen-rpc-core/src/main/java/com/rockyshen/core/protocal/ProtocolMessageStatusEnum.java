package com.rockyshen.core.protocal;

import lombok.Getter;

/**
 * @author rockyshen
 * @date 2024/11/14 11:01
 * ProtocolMessage中private byte status对应的枚举类
 */
@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok",20),
    BAD_REQUEST("badRequest",40),
    BAD_RESPONSE("badResponse",50)
    ;

    private final String text;
    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    // 根据传进来的状态码，返回枚举类实例
    public static ProtocolMessageStatusEnum getEnumByValue(int value){
        for(ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()){
            if(anEnum.value == value){
                return anEnum;
            }
        }
        return null;
    }
}
