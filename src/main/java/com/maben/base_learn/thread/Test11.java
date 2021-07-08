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
    public static void main(String[] args) {
        //第一种拒绝策略：死等
        final ThreadPool threadPool = new ThreadPool(2, 1000, TimeUnit.MILLISECONDS, 3, (taskQueues,task)->{
            //这里写用户自己的策略
            taskQueues.put(task);
        });
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            threadPool.execute(() -> {
                Sleeper.sleep(55);
                log.info("@@@ {}", finalI);
            });

        }
        Sleeper.sleep(3);
        threadPool.execute(() -> {
            log.info("@@@ {}", "999999");
        });
    }
}

/**
 * 创建线程池
 */
@Slf4j(topic = "m.ThreadPool")
class ThreadPool {
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
     * 拒绝策略
     */
    private RejectPolicy<Runnable> rejectPolicy;

    /**
     * 构造方法
     * @param coreSize 线程池大小
     * @param timeout 超时时间
     * @param timeUnit 单位
     * @param queueCapacity 阻塞队列容量上限
     */
    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    /**
     * 执行任务
     * @param task task
     */
    public void execute(Runnable task) {
        /*
        当前任务数没有超过 coreSize ，直接交给worker执行；
        当前任务数已经超过 coreSize ，加入队列暂存；
         */
        synchronized (workers) {
            if (workers.size() < coreSize) {
                final Worker worker = new Worker(task);
                log.info("新增worker，{},{}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
                //                this.taskQueue.put(task);
                /*
                如果队列满了，可以采用以下处理方式：
                    1.一直等待
                    2.待超时的等待
                        如果超时：
                            1.让调用者放弃执行任务
                            2.让调用者抛出异常
                            3.让调用者自己执行任务
                 也就是说这里会存在好多好多用户自己的操作，这样的话我们就把具体的方法权限交给用户，实现Java的策略模式；
                 定义一个接口，让用户自己去实现。
                 */
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }

    class Worker extends Thread {
        private Runnable task;

        /**
         * 构造方法
         * @param task 任务对象
         */
        public Worker(Runnable task) {
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
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                log.info("正在执行。。。{}", task);
                task.run();
                task = null;
            }
            synchronized (workers) {
                log.info("执行完毕，移除worker：{}", this);
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
                    if (nanos <= 0) {
                        return null;
                    }
                    //返回的是剩余的时间，防止虚假唤醒
                    log.info("队列为空，等待获取剩余时间：{} 纳秒", nanos);
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
    public void put(T task) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    log.info("队列已满，等待添加队列。。。{}", task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("put添加队列成功，唤醒空等待。。。{}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时时间的添加阻塞任务
     */
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            //将timeout转换成纳秒
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capacity) {
                try {
                    if (nanos <= 0) {
                        return false;
                    }
                    log.info("队列已满，等待添加队列。。。{}", task);
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("offer添加队列成功，唤醒空等待。。。{}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
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

    /**
     * 带有拒绝策略的添加
     * @param rejectPolicy
     * @param task
     */
    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            //判断队列是否已满
            if (queue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {
                log.info("tryPut添加队列成功，唤醒空等待。。。{}", task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}

/**
 * 队列已满时，用户选择自己的处理方式
 * @param <T> t
 */
@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> blockingQueue, T task);
}
