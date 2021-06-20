package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock学习--条件变量
 */
@Slf4j(topic = "m.Test09")
public class Test09 {
    private static boolean hasCigarette = false;
    private static boolean hasTakeOut = false;
    private static ReentrantLock ROOM  = new ReentrantLock();
    private static Condition cigaretteCondition = ROOM.newCondition();
    private static Condition takeOutCondition = ROOM.newCondition();

    public static void main(String[] args){

        new Thread(()->{
            ROOM.lock();
            try {
                log.info("查看有烟没有，{}",hasCigarette);
                while (!hasCigarette){
                    log.info("没烟，进入休息室休息");
                    try {
                        cigaretteCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("有烟了，可以开始干活了。。");
            }finally {
                ROOM.unlock();
            }
        },"小南").start();

        new Thread(()->{
            ROOM.lock();
            try {
                log.info("查看有外卖没有，{}",hasTakeOut);
                while (!hasTakeOut){
                    log.info("没外卖，进入休息室休息");
                    try {
                        takeOutCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("有外卖了，可以开始干活了。。");
            }finally {
                ROOM.unlock();
            }
        },"小女").start();

        Sleeper.sleep(2);
        new Thread(()->{
            ROOM.lock();
            try {
                log.info("外卖到了。。。");
                hasTakeOut=true;
                takeOutCondition.signal();
            }finally {
                ROOM.unlock();
            }
        }).start();

        Sleeper.sleep(2);
        new Thread(()->{
            ROOM.lock();
            try {
                log.info("烟到了。。。");
                hasCigarette=true;
                cigaretteCondition.signal();
            }finally {
                ROOM.unlock();
            }
        }).start();

    }
}
