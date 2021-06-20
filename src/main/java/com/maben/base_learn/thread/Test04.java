package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * 活锁现象
 */
@Slf4j(topic = "m.Test04")
public class Test04 {
    static int i = 10;
    public static void main(String[] args){

        new Thread(()->{
            while (i>0){
                Sleeper.sleep(1);
                log.info("i:"+i);
                i--;
            }
        },"t1").start();
        new Thread(()->{
            while (i<20){
                Sleeper.sleep(1);
                log.info("i:"+i);
                i++;
            }
        },"t2").start();
    }
}
