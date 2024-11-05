package com.rockyshen.consumer;

import com.rockyshen.model.User;
import com.rockyshen.core.proxy.ServiceProxyFactory;
import com.rockyshen.service.UserService;

/**
 * @author rockyshen
 * @date 2024/10/30 00:13
 * 服务消费者，启动类，调用接口方法
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        User user = new User();   // 空参构造
        user.setName("shen");
        user.setAge(31);
        user.setSex("male");
        /* RPC最终实现的效果
            可以通过userService接口，调用到userServiceImpl
            获得实现类的能力，也即打印用户名。
            目前是不行的，因为没有引入provider，不具备通信能力
         */
        User newUser = null;
        try {
            UserService userService = ServiceProxyFactory.getProxy(UserService.class);
            // 像调用本地方法一样，调用不同module里的方法
            newUser = userService.getUser(user);   // getUser()方法成功的话，会打印用户名字
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(newUser != null){
            System.out.println("远程调用RPC生效，成功调用了provider下的userServiceImpl");
            System.out.println(newUser.getName());
        }else{
            System.out.println("newUser为空，远程调用RPC未生效");
        }
    }
}