package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * JDK自带线程池测试
 */
@Slf4j(topic = "m.Test12")
public class Test12 {
    public static void main(String[] args) throws Exception {
        final ExecutorService pool = Executors.newFixedThreadPool(2);
        final Object any = pool.invokeAny(Arrays.asList(() -> {
                    log.info("begin2");
                    Sleeper.sleep(2);
                    log.info("end2");

                    return "begin1";
                }, () -> {
                    log.info("begin3");
                    Sleeper.sleep(3);
                    log.info("end3");
                    return "begin2";
                }, () -> {
                    log.info("begin4");
                    Sleeper.sleep(4);
                    log.info("end4");
                    return "begin3";
                }

        ));
        log.info("返回结果：{}", any);
    }

    private static void invokeAllTest(ExecutorService pool) throws InterruptedException {
        final List<Future<Object>> futures = pool.invokeAll(Arrays.asList(() -> {
                    log.info("begin1");
                    Sleeper.sleep(2);
                    return "begin1";
                }, () -> {
                    log.info("begin2");
                    Sleeper.sleep(3);
                    return "begin2";
                }, () -> {
                    log.info("begin3");
                    Sleeper.sleep(4);
                    return "begin3";
                }

        ));
        futures.forEach((future) -> {
            try {
                log.info("返回结果：{}", future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void submitTest(ExecutorService pool) throws InterruptedException, java.util.concurrent.ExecutionException {
        final Future<String> future = pool.submit(() -> {
            log.info("running");
            Sleeper.sleep(1);
            return "ok";
        });
        log.info("线程返回结果：{}", future.get());
    }
}
