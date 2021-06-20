package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * 死锁现象
 */
@Slf4j(topic = "m.Test03")
public class Test03 {
    public static void main(String[] args){
        Object a = new Object();
        Object b = new Object();
        new Thread(()->{
            synchronized (a){
                log.info("获取锁资源A");
                Sleeper.sleep(2);
                log.info("需要获取锁资源B，完成操作");
                synchronized (b){
                    log.info("获取锁资源B");
                }
            }
        }).start();
        new Thread(()->{
            synchronized (b){
                log.info("获取锁资源B");
                Sleeper.sleep(1);
                log.info("需要获取锁资源A，完成操作");
                synchronized (a){
                    log.info("获取锁资源A");
                }
            }
        }).start();
    }
}
