package com.easyaccounting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 极简记账后端服务入口类
 *
 * <p>初始化 Spring Boot 上下文，扫描 MyBatis Mapper 接口，并开启事务管理。</p>
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableRetry
public class EasyAccountingApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyAccountingApplication.class, args);
    }

}
