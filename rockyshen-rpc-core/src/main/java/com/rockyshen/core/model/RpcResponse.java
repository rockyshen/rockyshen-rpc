package com.rockyshen.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author rockyshen
 * @date 2024/11/1 13:04
 * 接收web服务器的请求的【实体类模型】
 * 先完成RpcRequest处理请求
 * 2、再到本类，封装响应结果！  1、返回值；2、返回值类型；3、响应信息；4、调用异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
//    private static final long serialVersionUID = 1L;

    private Object data;

    private Class<?> dataType;

    private String description;

    private Exception exception;
}
