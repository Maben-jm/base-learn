package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * reentrantLock学习-可打断性
 */
@Slf4j(topic = "m.Test06")
public class Test06 {
    private static final ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args){
        final Thread t1 = new Thread(() -> {
            try {
                log.info("尝试获取锁资源");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("线程被打断");
                return;
            }
            try {
                log.info("执行操作逻辑");
            }finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        t1.start();

        Sleeper.sleep(3);
        log.info("主线程睡眠3s后，打断t1线程");
        t1.interrupt();
    }
}
