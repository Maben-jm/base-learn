package com.maben.base_learn.jdk;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 测试jdk8日期类
 */
public class Test001 {
    public static void main(String[] args){

        //获取当前时间
        final LocalDateTime now = LocalDateTime.now();
        System.out.println(now);

        //获取当天18点的时间
        final LocalDateTime now18 = now.withHour(18);
        System.out.println(now18);

        //获取本周周四时间
        final LocalDateTime time1 = now.with(DayOfWeek.THURSDAY);
        System.out.println(time1);

        //获取上周四时间
        final LocalDateTime time2 = time1.plusWeeks(-1);
        System.out.println(time2);

        //两个时间之间的差
        System.out.println(Duration.between(now, now18).toHours());

    }
}
