package com.rockyshen.service;

import com.rockyshen.model.User;

/**
 * @author rockyshen
 * @date 2024/10/30 12:46
 * 用户服务接口：定义一个规约，getUser()
 */
public interface UserService {
    User getUser(User user);

    // 接口内部默认实现方法，不用被实现类
    default short getNumber(){
        return 1;
    }
}
