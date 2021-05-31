# base-learn

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

