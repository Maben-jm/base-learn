package com.maben.base_learn.util;

/**
 * 单例模式
 */
public final class Singleton {
    private Singleton(){}
    private static volatile Singleton INSTANCE=null;
    public static Singleton getInstance(){
        if (INSTANCE==null){
            synchronized (Singleton.class){
                if (INSTANCE==null){
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }
}
