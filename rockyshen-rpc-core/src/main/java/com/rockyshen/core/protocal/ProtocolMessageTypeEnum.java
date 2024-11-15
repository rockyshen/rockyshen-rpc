package com.rockyshen.core.protocal;

import lombok.Getter;

/**
 * @author rockyshen
 * @date 2024/11/14 11:06
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3)
    ;

    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    public static ProtocolMessageTypeEnum getEnumByKey(int key){
        for(ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()){
            if(anEnum.key == key){
                return anEnum;
            }
        }
        return null;
    }
}
