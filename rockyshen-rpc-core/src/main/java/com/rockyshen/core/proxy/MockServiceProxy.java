package com.rockyshen.core.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author rockyshen
 * @date 2024/11/6 10:22
 *
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke()",method.getName());

        return getDefaultObject(returnType);
    }

    private Object getDefaultObject(Class<?> returnType){
        if(returnType.isPrimitive()){
            if(returnType == int.class){
                return 0;
            }else if(returnType == short.class){
                return (short)0;
            }else if(returnType == long.class){
                return (long)0;
            }else if(returnType == boolean.class){
                return false;
            }
        }
        return null;
    }
}
