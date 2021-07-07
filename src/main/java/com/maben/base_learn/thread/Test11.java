package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义线程池
 */
@Slf4j(topic = "m.Test11")
public class Test11 {
    public static void main(String[] args){
        //创建线程池
        final ThreadPool threadPool = new ThreadPool(2,1000,TimeUnit.MILLISECONDS,10);
        for (int i = 0; i < 15; i++) {
            int finalI = i;
            threadPool.excute(()->{
                Sleeper.sleep(5);
                log.info("@@@ {}",finalI);
            });

        }
        Sleeper.sleep(3);
        threadPool.excute(()->{
            log.info("@@@ {}","999999");
        });
    } 
}

/**
 * 创建线程池
 */
@Slf4j(topic = "m.ThreadPool")
class ThreadPool{
    /**
     * 任务队列
     */
    private BlockingQueue<Runnable> taskQueue;
    /**
     * 线程集合
     */
    private HashSet<Worker> workers = new HashSet<Worker>();

    /**
     * 核心线程数
     */
    private int coreSize;

    /**
     * 获取任务超时时间
     */
    private long timeout;
    private TimeUnit timeUnit;

    /**
     * 构造方法
     * @param coreSize 线程池大小
     * @param timeout 超时时间
     * @param timeUnit 单位
     * @param queueCapacity 阻塞队列容量上限
     */
    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit,int queueCapacity) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
    }

    /**
     * 执行任务
     * @param task task
     */
    public void excute(Runnable task){
        /*
        当前任务数没有超过 coreSize ，直接交给worker执行；
        当前任务数已经超过 coreSize ，加入队列暂存；
         */
        synchronized (workers){
            if (workers.size()<coreSize){
                final Worker worker = new Worker(task);
                log.info("新增worker，{},{}",worker,task);
                workers.add(worker);
                worker.start();
            }else {
                this.taskQueue.put(task);
            }
        }
    }

    class Worker extends Thread{
        private Runnable task;

        /**
         * 构造方法
         * @param task 任务对象
         */
        public Worker(Runnable task){
            this.task = task;
        }

        /**
         * 重写run方法
         */
        @Override
        public void run() {
            /*
            当task不为空，直接执行任务；
            当task为空，从阻塞队列中获取任务并执行；
             */
            while (task!=null || (task = taskQueue.poll(timeout,timeUnit))!=null){
                log.info("正在执行。。。{}",task);
                task.run();
                task = null;
            }
            synchronized (workers){
                log.info("执行完毕，移除worker：{}",this);
                workers.remove(this);
            }
        }
    }

}

/**
 * 创建阻塞队列类
 * @param <T>
 */
@Slf4j(topic = "m.BlockingQueue")
class BlockingQueue<T> {
    /**
     * 1.创建任务队列
     *      用来存储当前任务
     */
    private Deque<T> queue = new ArrayDeque<>();
    /**
     * 2.创建锁
     *      用来保护队头和队尾的任务
     */
    private ReentrantLock lock = new ReentrantLock();
    /**
     * 3.创建生产者条件变量
     *      用来判断生产者是否等待
     */
    private Condition fullWaitSet = lock.newCondition();
    /**
     * 4.创建消费者条件变量
     *      用来判断消费者是否等待
     */
    private Condition emptyWaitSet = lock.newCondition();
    /**
     * 5.创建容量
     *      用来限定阻塞队列容量大小
     */
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 获取阻塞任务
     */
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            //将timeout转换成纳秒
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    if (nanos<=0){
                        return null;
                    }
                    //返回的是剩余的时间，防止虚假唤醒
                    log.info("队列为空，等待获取剩余时间：{} 纳秒",nanos);
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("获取队列第一个成功，唤醒队列满等待。。。。。。");
            final T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取阻塞任务
     */
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    log.info("队列为空，等待。。。");
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("获取队列第一个成功，唤醒队列满等待。。。");
            final T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加阻塞任务
     */
    public void put(T element) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    log.info("队列已满，等待添加队列。。。{}",element);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("添加队列成功，唤醒空等待。。。{}",element);
            queue.addLast(element);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取容量大小
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
