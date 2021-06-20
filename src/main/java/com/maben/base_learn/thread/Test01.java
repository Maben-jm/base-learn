package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试wait&notify
 */
@Slf4j(topic = "m.Test01")
public class Test01 {
    final static Object obj = new Object();
    public static void main(String[] args){
        new Thread(()->{
            synchronized (obj){
                log.info("开始执行。。。");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("结束执行。。。");
            }
        },"t1").start();

        new Thread(()->{
            synchronized (obj){
                log.info("开始执行。。。。");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("结束执行。。。。");
            }
        },"t2").start();
        Sleeper.sleep(1);
        log.info("主线程唤醒其他线程");
        synchronized (obj){
//            obj.notify();//只能唤醒一个

            /*
                唤醒所有线程，线程t1和t2竞争锁;
                t1先得到锁先执行完成后释放锁，
                t2继而得到锁，继续执行，
                直到所有线程都执行完成
            */
            obj.notifyAll();
        }
    }
}
