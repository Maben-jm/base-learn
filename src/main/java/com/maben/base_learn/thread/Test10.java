package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * 可见性测试
 */
@Slf4j(topic = "m.Test10")
public class Test10 {
    volatile static boolean flag = true;
    public static void main(String[] args){
        new Thread(()->{
            while (flag){
            }
        },"T1").start();


        Sleeper.sleep(7);
        log.info("主线程将flag参数改为false");
        flag = false;
    }
}
