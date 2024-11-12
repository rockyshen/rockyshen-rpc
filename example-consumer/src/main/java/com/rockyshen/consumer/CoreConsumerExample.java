package com.rockyshen.consumer;

import com.rockyshen.core.RpcApplication;
import com.rockyshen.core.config.RpcConfig;
import com.rockyshen.core.proxy.ServiceProxyFactory;
import com.rockyshen.core.register.Registry;
import com.rockyshen.core.utils.ConfigUtils;
import com.rockyshen.model.User;
import com.rockyshen.service.UserService;

/**
 * @author rockyshen
 * @date 2024/11/5 16:11
 */
public class CoreConsumerExample {
    public static void main(String[] args) {
        /* 只有调用了ConfigUtils方法，才会读取当前目录的配置文件；
            这里只是测试，正常情况下，应该是RpcApplication.init()在哪里运行，
            就在哪里读取配置文件；
            所以RpcApplication.init()是在Provide模块运行的，所以读的是Provide模块下的application.properties
         */
//        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
//        System.out.println(rpc);

        // 不管是consumer还是provider，都要执行RpcApplication的初始化
        RpcApplication.init();

        User user = new User();
        user.setName("rockyshen");

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User newUser1 = userService.getUser(user);     // 第一次查注册中心
        User newUser2 = userService.getUser(user);    // 第二次查缓存
        User newUser3 = userService.getUser(user);     // 第三次下线provider



        // 如果mock开启，就不返回userServiceProxy对象了，拦截掉了
        if(newUser1 != null){
            System.out.println(newUser1.getName());
        }else{
            System.out.println("未获取到ServiceProxy对象，user为空");
        }

        // 此处如果打印1，表示走了MockServiceProxy；如果打印0，表示走了userService自己的方法
//        short number = userService.getNumber();
//        System.out.println(userService.getNumber());

    }
}
