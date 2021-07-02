package com.example.longpollingdemo.controller;

import com.example.longpollingdemo.dto.LongPolling;
import com.example.longpollingdemo.executor.ConfigExecutor;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 长轮询主要业务
 */
@CrossOrigin
@Slf4j
@RestController
public class LongPollingController {

    private volatile Multimap<String, LongPolling> longPollingMultimap = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    /**
     * 长轮询接口
     */
    @GetMapping("longPolling")
    public void longPolling(String dataId
            , HttpServletRequest request
            , HttpServletResponse response) {
        // 开启异步
        log.info("开启异步 dataId: {}", dataId);
        AsyncContext asyncContext = request.startAsync(request, response);

        // 将长轮询对象保存下来
        LongPolling longPolling = new LongPolling(asyncContext, new AtomicBoolean(true));
        longPollingMultimap.put(dataId, longPolling);

        // 设置一个定时器 30s 后超时响应 304
        ConfigExecutor.scheduleLongPolling(() -> {
            if (longPolling.getTimeout().get()) {
                longPollingMultimap.remove(dataId, longPolling);
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                asyncContext.complete();
            }
            log.info("LongPolling, dataId: {}, LocalDateTime: {}, 超时!", dataId, LocalDateTime.now());
        }, 30, TimeUnit.SECONDS);
    }

    /**
     * 发布配置
     */
    @SneakyThrows
    @GetMapping("/publish")
    public String publish(String dataId, String data) {
        log.info("publish dataId: [{}], data: [{}]", dataId, data);

        // 移除所有传入的dataId, 并得到LongPolling
        Collection<LongPolling> longPollings = longPollingMultimap.removeAll(dataId);
        for (LongPolling longPolling : longPollings) {
            AtomicBoolean timeout = longPolling.getTimeout();
            // 先判断超时是 true, 再将超时关闭
            if (timeout.get() && timeout.compareAndSet(true, false)) {
                // 拼接返回内容并放开异步
                AsyncContext asyncContext = longPolling.getAsyncContext();
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(data);
                asyncContext.complete();
            }
        }

        return "success";
    }

}
