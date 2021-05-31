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
