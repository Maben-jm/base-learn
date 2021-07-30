package com.maben.base_learn.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * AQS学习
 */
@Slf4j(topic = "m.TestAQS")
public class TestAQS {
    public static void main(String[] args){
        MyLock lock = new MyLock();
        new Thread(()->{
            try {
                lock.lock();
                log.info("t1 lock");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                lock.lock();
//                log.info("t1 测试锁的不可重用性");
            }finally {
                log.info("t1 unlock");
                lock.unlock();
            }
        },"t1").start();


        new Thread(()->{
            try {
                lock.lock();
                log.info("t2 lock");
            }finally {
                log.info("t2 unlock");
                lock.unlock();
            }
        },"t2").start();
    }
}

/**
 * 自定义不可重用锁
 */
class MyLock implements Lock {

    class MySysn extends AbstractQueuedSynchronizer{

        /**
         * 尝试加锁
         */
        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0,1)){
                //加锁成功
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 尝试释放锁
         */
        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        /**
         * 是否持有独占锁
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() ==1;
        }
        public Condition newCondition(){
            return new ConditionObject();
        }
    }
    private MySysn mySysn = new MySysn();

    /**
     * 加锁方法
     * 不可打断
     * 不成功进入等待队列等待
     */
    @Override
    public void lock() {
        mySysn.acquire(1);
    }

    /**
     * 加锁方法  可打断
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        mySysn.acquireInterruptibly(1);
    }

    /**
     * 尝试加锁，不成功直接返回false
     * @return
     */
    @Override
    public boolean tryLock() {
        return mySysn.tryAcquire(1);
    }

    /**
     * 带超时加锁
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mySysn.tryAcquireNanos(1,unit.toNanos(time));
    }

    /**
     * 解锁
     */
    @Override
    public void unlock() {
        mySysn.release(1);
    }

    /**
     * 创建条件变量
     */
    @Override
    public Condition newCondition() {
        return mySysn.newCondition();
    }
}
