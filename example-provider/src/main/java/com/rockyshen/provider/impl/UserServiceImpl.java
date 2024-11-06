package com.rockyshen.provider.impl;

import com.rockyshen.model.User;
import com.rockyshen.service.UserService;

/**
 * @author rockyshen
 * @date 2024/10/30 12:53
 * 实现UserService定义的getUser接口规约方法
 * 实现逻辑：打印用户名，并返回这个用户
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("我是UserService的实现类 -> "+"用户名："+ user.getName());
        return user;
    }
}
