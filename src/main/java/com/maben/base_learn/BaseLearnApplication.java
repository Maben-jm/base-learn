package com.maben.base_learn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 本项目主要用于学习Java基础的知识
 */
@Slf4j(topic = "m.BaseLearnApplication")
@SpringBootApplication
public class BaseLearnApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseLearnApplication.class, args);
        log.info("**启动成功**");
        log.info("**本项目主要用于学习Java基础的知识**");
    }
}
