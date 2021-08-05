[TOC]

# 工具类

## Sleeper

### 代码

````java
package com.maben.base_learn.util;

import java.util.concurrent.TimeUnit;

public class Sleeper {
    public static void sleep(int i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(double i) {
        try {
            TimeUnit.MILLISECONDS.sleep((int) (i * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
````

## 基于spring的PropertiesUtil

### 代码

```java
package com.css.hashlog.util;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Properties;

/**
 * 直接读取resources下的配置
 */
public class PropertiesUtil {
    public static Properties props;

    static {
        try {
            readPropertiesFile("/config.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties readPropertiesFile(String filePath) throws FileNotFoundException, IOException {
        InputStream inputStream = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource(filePath);
            inputStream = classPathResource.getInputStream();
            props = new Properties();
            props.load(new InputStreamReader(inputStream, "UTF-8"));
            return props;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static String getValue(String key) {
        return props.getProperty(key);
    }

}
```

## 雪花算法生成唯一ID

### 代码

````java
package com.maben.remote_debug.test;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
public class SnowflakeIdWorker {

    // ==============================Fields===========================================
    /** 开始时间截 (2021-07-16) */
    private final long twepoch = 1626424350023l;

    /** 机器id所占的位数 */
    private final long workerIdBits = 5L;

    /** 数据标识id所占的位数 */
    private final long dataCenterIdBits = 5L;

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 支持的最大数据标识id，结果是31 */
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    /** 序列在id中占的位数 */
    private final long sequenceBits = 12L;

    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;

    /** 数据标识id向左移17位(12+5) */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;

    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 工作机器ID(0~31) */
    private long workerId;

    /** 数据中心ID(0~31) */
    private long dataCenterId;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    private static SnowflakeIdWorker idWorker;

    static {
        idWorker = new SnowflakeIdWorker(getWorkId(),getDataCenterId());
    }

    //==============================Constructors=====================================
    /**
     * 构造函数
     * @param workerId 工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    public SnowflakeIdWorker(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }else {
            //时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    private static Long getWorkId(){
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for(int b : ints){
                sums += b;
            }
            return (long)(sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0,31);
        }
    }

    private static Long getDataCenterId(){
        int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
        int sums = 0;
        for (int i: ints) {
            sums += i;
        }
        return (long)(sums % 32);
    }


    /**
     * 静态工具类
     *
     * @return
     */
    public static synchronized Long generateId(){
        long id = idWorker.nextId();
        return id;
    }

    //==============================Test=============================================
    /** 测试 */
    public static void main(String[] args) throws Exception {
        System.out.println(System.currentTimeMillis());
        long startTime = System.nanoTime();
        for (int i = 0; i < 50000; i++) {
//            TimeUnit.SECONDS.sleep(1);
            long id = SnowflakeIdWorker.generateId();
            System.out.println(id);
        }
        System.out.println((System.nanoTime()-startTime)/1000000+"ms");
    }
}
````

## String类

### 代码

````java
package com.maben.remote_debug.test;

/**
 * String.format()
 */
public class MTest002 {
    public static void main(String[] args) {
        System.out.println(String.format("hi:string->%s,%s", "1", "2"));
        System.out.println(String.format("hi:char->%c", 'x'));
        System.out.println(String.format("hi:boolean->%b", true));
        System.out.println(String.format("hi:整数类型(十进制)->%d", 100));
        System.out.println(String.format("hi:整数类型(十六进制)->%x", 100));
        System.out.println(String.format("hi:整数类型(八进制)->%o", 100));
        System.out.println(String.format("hi:浮点类型->%f", 8.88));
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
        /*
            搭配转换符还有实现高级功能
         */
        // +   为正数或者负数添加符号
        System.out.println(String.format("hi:+使用->%+d", 100));
        // 0    数字前面补0(加密常用)
        System.out.println(String.format("hi:补0方法->%010d", 111));
        // 空格  在整数之前添加指定数量的空格
        System.out.println(String.format("hi:补 方法->% 10d", 111));
        // ,    以“,”对数字分组(常用显示金额)
        System.out.println(String.format("hi,','方法->%,d", 1000000000));
    }
}
````

### 结果

```java
hi:string->1,2
hi:char->x
hi:boolean->true
hi:整数类型(十进制)->100
hi:整数类型(十六进制)->64
hi:整数类型(八进制)->144
hi:浮点类型->8.880000
++++++++++++++++++++++++++++++++++++++++++++++++++++
hi:+使用->+100
hi:补0方法->0000000111
hi:补 方法->       111
hi,','方法->1,000,000,000
```

## 国密SM3

### 工具类

````java
package com.maben.base_learn.util;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.security.Security;
import java.util.Objects;

public class Sm3Util {
    private static final String ENCODING = "UTF-8";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * SM3算法加密
     * @param data 数据字符串
     * @return 返回固定长度=32的16进制字符串
     */
    public static String encrypt(String data) throws Exception {
        //将返回的hash值转成16进制字符串
        String result = null;
        //将字符串转换成byte数组
        final byte[] bytes = data.getBytes(ENCODING);
        //调用hash()
        final byte[] resBytes = hash(bytes);
        //将返回值转化为16进制数组
        result = ByteUtils.toHexString(resBytes);
        return result;
    }

    /**
     * 一次性
     * @param bytes 数据bytes
     * @return 返回长度为32的byte数组
     */
    private static byte[] hash(byte[] bytes) {
        final SM3Digest digest = new SM3Digest();
        digest.update(bytes, 0, bytes.length);
        byte[] out = new byte[digest.getDigestSize()];
        digest.doFinal(out, 0);
        return out;
    }

    public static String encrypt(File file) throws Exception {
        //调用hash()
        final byte[] resBytes = hash(file);
        if (Objects.isNull(resBytes)) {
            throw new Exception("生成SM3的HASH值有异常，请查看日志");
        }
        //将返回的hash值转成16进制字符串
        return ByteUtils.toHexString(resBytes);
    }

    /**
     * 分段式
     * @param file file
     * @return 返回长度为32的byte数组
     */
    private static byte[] hash(File file) {
        try (FileInputStream fis = new FileInputStream(file);) {
            final SM3Digest digest = new SM3Digest();
            final byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer, 0, buffer.length)) != -1) {
                digest.update(buffer, 0, length);
            }
            byte[] out = new byte[digest.getDigestSize()];
            digest.doFinal(out, 0);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final KeyParameter secretKey = new KeyParameter(Hex.decode("1234"));

    /**
     * hmac-sm3  分段式加密
     * @param file file
     * @return 返回长度为32的byte数组
     * @throws Exception 。。
     */
    public static String encryptHmac(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            final SM3Digest digest = new SM3Digest();
            HMac mac = new HMac(digest);
            mac.init(secretKey);
            mac.reset();
            final byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer, 0, buffer.length)) != -1) {
                mac.update(buffer, 0, length);
            }
            final byte[] out = new byte[mac.getMacSize()];
            mac.doFinal(out, 0);
            return Hex.toHexString(out);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * hmac-sm3  字符串加密
     * @param data data
     * @return 返回长度为32的byte数组
     * @throws Exception 。。
     */
    public static String encryptHmac(String data) throws Exception {
        final SM3Digest digest = new SM3Digest();
        HMac mac = new HMac(digest);
        mac.init(secretKey);
        mac.reset();
        final byte[] bytes = data.getBytes(ENCODING);
        mac.update(bytes, 0, bytes.length);
        final byte[] out = new byte[mac.getMacSize()];
        mac.doFinal(out, 0);
        return Hex.toHexString(out);
    }

}
````

### 测试类

````java
package com.maben.base_learn;

import com.maben.base_learn.util.Sm3Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@Slf4j(topic = "m.Sm3Test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaseLearnApplication.class)
public class Sm3Test {
    /**
     * 测试SM3加密字符串
     * 3facd4e9935b73112ee4ce95bc9797c78c313357d4d22f91259cd601106cf8a3
     */
    @Test
    public void test001()throws Exception{
        log.info("测试加密字符串：{}", Sm3Util.encrypt("哈哈哈哈"));
    }
    /**
     * 测试SM3加密文件
     * 69313e4c559d2bd917aadc204062b5eccb17380957b94abfa6a8f882520d453c
     */
    @Test
    public void test002()throws Exception{
        log.info("测试加密文件结果：{}",Sm3Util.encrypt(new File("xxx.txt")));
    }
    /**
     * 测试HMAC-SM3加密字符串
     * 62548f61aa0a00aa7c80e7e196a62b4e18e4b0490dc3228042d9baee5d39aa9f
     */
    @Test
    public void test003()throws Exception{
        log.info("测试HMAC-SM3加密字符串结果：{}",Sm3Util.encryptHmac("哈哈哈哈"));
    }

    /**
     * 测试HMAC-SM3加密文件
     * 79d6d3d66c26343b245cbe7356c955061048bebf90e802217e0027f49ebf95fc
     * @throws Exception 。。
     */
    @Test
    public void test004()throws Exception{
        log.info("测试HMAC-SM3加密文件结果：{}",Sm3Util.encryptHmac(new File("xxx.txt")));
    }
}
````

