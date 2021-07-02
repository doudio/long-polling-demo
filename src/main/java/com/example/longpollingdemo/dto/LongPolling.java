package com.example.longpollingdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.AsyncContext;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 长轮询状态异步对象
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LongPolling {

    private AsyncContext asyncContext;

    private AtomicBoolean timeout;

}
