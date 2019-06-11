package com.dtstack.dbhaswitch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@MapperScan(basePackages = {"com.dtstack.dbhaswitch.mapper"}, sqlSessionFactoryRef = "rdsliteSqlSessionFactory")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DbhaApplication {

    public static void main(String[] args) {
        System.out.println("rdslite-service init...");
        SpringApplication.run(DbhaApplication.class, args);
    }
}
