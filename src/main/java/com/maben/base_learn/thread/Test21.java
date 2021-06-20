package com.maben.base_learn.thread;

import com.maben.base_learn.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * 使用多线程模拟生产者-消费者模式
 */
@Slf4j(topic = "m.Test21")
public class Test21 {
    public static void main(String[] args){
        final MessageQueue messageQueue = new MessageQueue(2);
        //创建三个生产者线程
        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(()->{
                messageQueue.put(new Message(id,"生产者消息内容"+id));
            },"produce-"+i).start();
        }
        //创建一个消费者线程
        new Thread(()->{
            while (true){
                Sleeper.sleep(1);
                messageQueue.take();
            }
        },"consumer").start();
    }
}

/**
 * 创建消息队列类
 */
@Slf4j(topic = "m.MessageQueue")
class MessageQueue{

    /**
     * 存放消息的集合（采用「先进先出」模式，使用链表）
     */
    private final LinkedList<Message> list = new LinkedList<>();
    /**
     * 设置集合最大容量
     */
    private int capacity;

    /**
     * 通过构造方法确定消息容量大小
     * @param capacity capacity
     */
    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 获取消息方法
     */
    public Message take(){
        synchronized (list){
            //检查队列是否为空
            while (list.isEmpty()){
                log.info("队列为空，不能消费");
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //取出头部第一个
            final Message message = list.removeFirst();
            log.info("消费消息：{}",message);
            //唤醒其他线程
            list.notifyAll();
            return message;
        }
    }
    /**
     * 存入消息
     */
    public void put(Message message){
        synchronized (list){
            //检查队列是否已满
            while (list.size()==capacity){
                log.info("队列已满，不能存入");
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //将消息添加到队列的尾部
            list.addLast(message);
            log.info("添加消息：{}",message);
            //唤醒等待的线程
            list.notifyAll();
        }
    }
}

/**
 * 创建消息类（确保属性不能修改,也不能有子类）
 */
final class Message{
    private int id;
    private Object content;

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", content=" + content + '}';
    }

    public int getId() {
        return id;
    }

    public Object getContent() {
        return content;
    }

    public Message(int id, Object content) {
        this.id = id;
        this.content = content;
    }
}
