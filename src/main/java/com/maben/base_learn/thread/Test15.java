package com.maben.base_learn.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * fork-join学习
 *  递归思想，将任务拆分成最小单元，然后再用新线程去执行
 */
public class Test15 {
    public static void main(String[] args){
        //默认线程数是：与当前机器CPU核数相同
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        System.out.println(forkJoinPool.invoke(new MyTask(5)));
        System.out.println(forkJoinPool.invoke(new MyTask2(1,5)));
    }
}


class MyTask extends RecursiveTask<Integer>{
    private int i;
    public MyTask(int i){this.i = i;}
    @Override
    protected Integer compute() {
        if (i==1){
            return i;
        }
        final MyTask task = new MyTask(i-1);
        task.fork();
        return task.join()+i;
    }
}

class MyTask2 extends RecursiveTask<Integer>{

    private int start;
    private int end;
    public MyTask2(int start ,int end){
        this.end = end;
        this.start = start;
    }
    @Override
    protected Integer compute() {
        if (start==end){
            return start;
        }
        if (end-start==1){
            return start+end;
        }
        int middle = (start+end)/2;
        final MyTask2 task1 = new MyTask2(start,middle);
        task1.fork();
        final MyTask2 task2 = new MyTask2(middle+1,end);
        task2.fork();
        return task1.join()+task2.join();
    }
}
