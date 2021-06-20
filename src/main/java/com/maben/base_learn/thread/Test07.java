package com.maben.base_learn.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * reentrantLock学习--超时性
 */
@Slf4j(topic = "m.Test07")
public class Test07 {
    private static final ReentrantLock LOCK = new ReentrantLock();
    public static void main(String[] args){
        final Thread t1 = new Thread(() -> {
            try {
                if (!LOCK.tryLock(3, TimeUnit.SECONDS)){
                    log.info("尝试获得锁资源失败，返回");
                    return;
                }
            } catch (InterruptedException e) {
                log.info("线程被打断，返回");
                e.printStackTrace();
                return;
            }
            try {
                log.info("执行逻辑代码");
            }finally {
                log.info("释放锁资源");
                LOCK.unlock();
            }
        }, "t1");

        log.info("主线程获取锁资源");
        LOCK.lock();
        t1.start();
    }
}
