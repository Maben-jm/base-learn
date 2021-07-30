package com.maben.base_learn.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 异步模式之工作线程：
 * 饥饿现象：
 *  线程池中的所有线程都以用完，并且都在等结果，但是没有处理线程去处理数据
 * 处理：
 *  不同的任务类型应该使用不同的线程池，避免饥饿现象
 */
@Slf4j(topic = "m.Test13")
public class Test13 {
    static final List<String> MENU = Arrays.asList("炒饭","炒饼","面条");
    static final Random RANDOM = new Random();
    static String cooking(){
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    public static void main(String[] args){
        final ExecutorService waiterPool = Executors.newFixedThreadPool(1);
        final ExecutorService cookingPool = Executors.newFixedThreadPool(1);

        waiterPool.execute(()->{
            log.info("处理点餐。。。");
            final Future<String> future = cookingPool.submit(() -> {
                log.info("做菜。。。");
                return cooking();
            });
            try {
                log.info("上菜：{}",future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        waiterPool.execute(()->{
            log.info("处理点餐。。。。");
            final Future<String> future = cookingPool.submit(() -> {
                log.info("做菜。。。。");
                return cooking();
            });
            try {
                log.info("上菜：：{}",future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
