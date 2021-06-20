package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * 多把锁学习
 */
@Slf4j(topic = "m.Test02")
public class Test02 {
    public static void main(String[] args){
        final BigRoom bigRoom = new BigRoom();
        new Thread(()->{
            bigRoom.study();
        },"A同学-学习").start();
        new Thread(()->{
            bigRoom.sleep();
        },"B同学-睡觉").start();
    }
}
@Slf4j(topic = "m.BigRoom")
class BigRoom{
    private final Object studyRoom = new Object();
    private final Object sleepRoom = new Object();
    public void sleep(){
        synchronized(sleepRoom){
            log.info("睡觉一小时");
            Sleeper.sleep(1);
        }
    }
    public void study(){
        synchronized(studyRoom){
            log.info("学习一小时");
            Sleeper.sleep(1);
        }
    }
}
