package com.maben.base_learn;

import com.maben.base_learn.util.Sm3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本项目主要用于学习Java基础的知识
 */
@Slf4j(topic = "m.BaseLearnApplication")
@SpringBootApplication
@RestController
public class BaseLearnApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseLearnApplication.class, args);
        log.info("**启动成功**");
        log.info("**本项目主要用于学习Java基础的知识**");
    }

    @GetMapping("sayHello")
    public String sayHello()throws Exception{
        return "hello ："+ Sm3Util.encrypt("123");
    }
}
