package com.rockyshen.core.protocal;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author rockyshen
 * @date 2024/11/14 11:11
 * 协议中指定序列化器的枚举类
 */
@Getter
public enum ProtocolMessageSerializerEnum {
    JDK(0,"jdk"),
    JSON(1,"json"),
    KRYO(2,"kryo"),
    HESSIAN(3,"hessian")
    ;

    private final int key;

    private final String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    // TODO 这个是干嘛的？
    public static List<String> getValues() {
        return null;
    }

    // 根据数字,返回枚举类
    public static ProtocolMessageSerializerEnum getEnumByKey(int key){
        for(ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()){
            if(anEnum.key == key){
                return anEnum;
            }
        }
        return null;
    }

    // 根据字符串，返回枚举类
    public static ProtocolMessageSerializerEnum getEnumByValue(String value){
        for(ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()){
            if(anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }
}
