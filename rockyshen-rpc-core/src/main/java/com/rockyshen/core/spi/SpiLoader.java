package com.rockyshen.core.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.rockyshen.core.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author rockyshen
 * @date 2024/11/7 11:40
 * 实现SPI机制，加载resource/META-INF下的文件
 */
@Slf4j
public class SpiLoader {
    /** key-value存储加载进来的序列化器实例
     *  key是字符串           接口的全限定类名
     *  value嵌套一个Map  --> implClassMap
     *      key是字符串        "jdk"
     *      value是一个类模版   该接口下实现类的类模版 Class<T>  ==> com.rockyshen.core.serializer.JdkSerializer
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 每个类加载器，只能new一次
     * 实例化完，就存在这个缓存里，避免不停的new新的出来！
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";
    private static final String[] SCAN_DIRS = new String[]{RPC_CUSTOM_SPI_DIR,RPC_SYSTEM_SPI_DIR};

    // 这里还是硬编码，未实现加载各种接口类型，目前只有一个接口在list中，就是Serializer.class
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    // 加载所有的接口，未使用！
    public static void loadAll(){
        log.info("加载所有SPI文件中指定的类");
        for(Class<?> clazz:LOAD_CLASS_LIST){
            load(clazz);   // 这里是Serializer.class，也就是接口的类模版哟！
        }
    }

    /**
     * 加载某个类型
     * 传递一个类模版，我将这个类模版存入implClassMap中！
     * @param type  Serializer接口的类模版！
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> type){
        log.info("加载类型为{}的SPI",type.getName());
        /**
         * implClassMap存放
         * jdk=com.rockyshen.core.serializer.JdkSerializer
         *  key: 配置的序列化器名字，string      key = "jdk"
         *  value: 这个实现类的全限定路径        value = com.rockyshen.core.serializer.JdkSerializer
         *
         */
        Map<String, Class<?>> implClassMap = new HashMap<>();

        for(String scanDir : SCAN_DIRS){
            // 基于Serializer接口，可以从文件中加载到所有实现类吗？
            // 读不到？为什么
            List<URL> resources = ResourceUtil.getResources(scanDir + type.getName());
            for(URL resource:resources){
                try {
                    // 字符流
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        String[] stringArr = line.split("=");   // 按“=”拆分 ->  jdk=com.yupi.yurpc.serializer.JdkSerializer
                        if(stringArr.length > 1){
                            String key = stringArr[0];
                            String className = stringArr[1];
                            // 将读取到的（字符串形式）类路径，转为一个真正的Class<?>
                            Class<?> clazz = Class.forName(className);
                            implClassMap.put(key,clazz);
                        }
                    }
                } catch (Exception e) {
                    log.error("SPI机制，加载resource时出错 --> ",e);
                }
            }
        }
        loaderMap.put(type.getName(),implClassMap);
        return implClassMap;
    }

    /**
     * 传递一个接口的clazz，和一个key
     * 从LoaderMap中基于接口找到所有实现类的Map
     * 再基于key，找到特定的那一个实现类！
     * @param clazz
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<?>clazz, String key){
        String clazzName = clazz.getName();
        Map<String, Class<?>> implClassMap = loaderMap.get(clazzName);

        // 基于接口，加载实体类的map，如果这个map是空的
        if(implClassMap == null){
            String errMsg = String.format("SpiLoader 未加载 %s 类型",clazzName);
            throw new RuntimeException(errMsg);
        }
        // 如果这个map不包含这个实现类对应key
        if(!implClassMap.containsKey(key)){
            String errMsg = String.format("SpiLoader 的 %s 不存在 key = %s的类型",clazzName,key);
        }

        Class<?> implClass = implClassMap.get(key);
        if(!instanceCache.containsKey(implClass.getName())){
            try {
                // 实例缓存中加载指定类型的实例
                instanceCache.put(implClass.getName(),implClass.newInstance());   // newInstance不推荐使用
                log.info("成功加载Serializer接口的实现类，为：{}",implClass.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败",implClass);
                throw new RuntimeException(errorMsg,e);
            }
        }
        // 缓存中有，直接返回
        return (T) instanceCache.get(implClass.getName());
    }
}
