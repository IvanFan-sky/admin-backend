package com.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Admin管理系统启动类
 * 
 * Spring Boot应用程序的主入口
 * 负责启动整个Admin管理系统
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.admin"})
public class AdminApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("Admin管理系统启动成功！");
    }
}