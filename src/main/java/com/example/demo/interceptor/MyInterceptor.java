package com.example.demo.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.demo.agent.TraceContext;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static com.example.demo.agent.MyAgent.LOCAL;

public class MyInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        try {
            // 原有函数执行
            return callable.call();
        } finally {
            TraceContext context = LOCAL.get();
            if(context != null){
                context.setMethodType(method.getDeclaringClass().getName()+"."+method.getName());
                context.setTime("调用方法时间:"+ (System.currentTimeMillis() - start) +"ms");
                //上报操作，rpc，这里还需要修改sql等等打印到logback也上报到收集中心
                System.out.println(JSON.toJSONString(context));
                context.clear();
            }
        }
    }

}
