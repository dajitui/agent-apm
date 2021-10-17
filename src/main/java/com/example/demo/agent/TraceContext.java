package com.example.demo.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author M
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceContext implements Serializable {
    private String traceId;
    private String spanId;
    private String parentSpaceId;
    private String time;
    private String methodType;
    private String data;
    //spanId separator
    public static final String RPC_ID_SEPARATOR = ".";

    /**
     * sub-context counter
     */
    private static AtomicInteger childContextIndex = new AtomicInteger(0);

    /**
     * 如果rpc调用的时候需要将spanid传递成这个方法的值
     *
     * @return
     */
    public String nextChildContextId() {
        return this.spanId + RPC_ID_SEPARATOR + childContextIndex.incrementAndGet();
    }

    public static TraceContext cloneContext(TraceContext context) {
        if(context==null){
            return new TraceContext();
        }
        return TraceContext.builder()
                .spanId(context.nextChildContextId())
                .parentSpaceId(context.getSpanId())
                .traceId(context.getTraceId())
                .build();
    }

    public void clear() {
        //上报之后需要清理之前的一些数据
        this.data = "";
    }
}
