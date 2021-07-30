package com.maben.base_learn.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度线程池
 *  1.延时执行任务（报错之后其余线程任然可以执行）
 *  2.定时执行任务
 */
@Slf4j(topic = "m.Test14")
public class Test14 {
    public static void main(String[] args){
        final ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);;
        /*
            定时执行任务
         */
        pool.scheduleWithFixedDelay(()->{
            log.info("定时任务scheduleWithFixedDelay：{}", DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },3,1,TimeUnit.SECONDS);

        /*
            定时执行任务
            如果到点上一个任务还没执行完，就会等上一个线程执行完
            执行完后立马执行下一个线程
         */
        pool.scheduleAtFixedRate(()->{
            log.info("定时任务scheduleAtFixedRate：{}", DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },3,1,TimeUnit.SECONDS);

        /*
            延时执行任务
            即使有的线程报错了，其余的线程也会继续执行完！
         */
        pool.schedule(()->{
            log.info("**********************************************");
            log.info("执行。。");
            int i =1/0;
        },1, TimeUnit.SECONDS);
        pool.schedule(()->{
            log.info("执行2.。。");
            log.info("**********************************************");
        },1,TimeUnit.SECONDS);
    }
}
