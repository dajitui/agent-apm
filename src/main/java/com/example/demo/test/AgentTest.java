package com.example.demo.test;

import com.alibaba.fastjson.JSON;
import com.example.demo.agent.TraceContext;

import java.util.UUID;

import static com.example.demo.agent.MyAgent.LOCAL;

public class AgentTest {

    private void fun1() throws Exception {
        TraceContext context = LOCAL.get();
        if (context != null) {
            //由于没有集成sleuth，spaceId需要自己模调用的时候简单的自增
            String spaceId = context.getSpanId();
            //rpc调用的时候需要+1
            context.setSpanId(spaceId);
            context.setParentSpaceId(spaceId);
            context.setData("fun1需要上报的数据");
        }
        System.out.println("this is fun 1.");
        Thread.sleep(500);
    }

    private void fun2() throws Exception {
        TraceContext context = LOCAL.get();
        if (context != null) {
            //由于没有集成sleuth，spaceId需要自己模调用的时候简单的自增
            String spaceId = context.getSpanId();
            //rpc调用的时候需要+1
            context.setSpanId(spaceId);
            context.setParentSpaceId(spaceId);
            if (!"".equals(context.getData())) {
                System.out.println("fun2可以拿到之前context上传数据：" + context.getData());
            }
        }
        System.out.println("this is fun 2.");
        Thread.sleep(500);
        //模拟调用rpc
        TraceContext rpcContext = TraceContext.cloneContext(context);
        System.out.println("上报rpc context:" + JSON.toJSONString(rpcContext));

    }


    /**
     * 可以重写logback append逻辑，打印日志也上报到收集数据的系统
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //实际开发由sleuth来生成traceId
        String traceId = UUID.randomUUID().toString();
        String spaceId = "0";
        TraceContext context = TraceContext.builder()
                .spanId(spaceId)
                .parentSpaceId("0")
                .traceId(traceId)
                .build();
        //如果是rpc的话，需要使用拦截器，将context塞到LOCAL里面
        LOCAL.set(context);
        AgentTest test = new AgentTest();
        test.fun1();
        test.fun2();
        //实际开发需要拦截器去删除本地变量
        LOCAL.remove();
    }
}
