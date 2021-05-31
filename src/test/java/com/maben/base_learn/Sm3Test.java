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
