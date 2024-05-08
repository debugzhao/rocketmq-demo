package com.example.rocketmqdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zhaojingchao
 * @Date 2024/05/08 13:56
 * @Email zhaojingchao@joysuch.com
 * @Desc
 */
@RestController
public class JMeterController {

    @GetMapping("test1")
    public String test1() {
        return "OK";
    }

    @GetMapping("test2")
    public String test2() throws InterruptedException {
        Thread.sleep(50L);
        return "OK";
    }
}
