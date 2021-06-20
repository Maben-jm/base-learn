package com.maben.base_learn.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * reentrantLock学习-可重入特性
 */
@Slf4j(topic = "m.Test05")
public class Test05 {
    private final static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args){
        lock.lock();
        try {
            log.info("enter main");
            m1();
        }finally {
            lock.unlock();
        }
    }
    public static void m1(){
        lock.lock();
        try {
            log.info("enter m1");
            m2();
        }finally {
            lock.unlock();
        }
    }
    public static void m2(){
        lock.lock();
        try {
            log.info("enter m2");
        }finally {
            lock.unlock();
        }
    }
}
