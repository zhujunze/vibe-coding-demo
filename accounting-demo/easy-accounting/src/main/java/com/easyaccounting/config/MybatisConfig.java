package com.easyaccounting.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置类
 *
 * <p>分离 Mapper 扫描配置，避免 WebMvcTest 加载数据库相关 Bean。</p>
 */
@Configuration
@MapperScan("com.easyaccounting.mapper")
public class MybatisConfig {
}
