package com.campus.lostfound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园失物招领平台启动类
 */
@SpringBootApplication
public class LostFoundApplication {

    public static void main(String[] args) {
        SpringApplication.run(LostFoundApplication.class, args);
        System.out.println("校园失物招领平台启动成功！");
        System.out.println("访问地址: http://localhost:8081");
    }
}