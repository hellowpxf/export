package com.pxf.count;

import com.pxf.count.service.SentinelTest;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @description:CountingApplication
 * @author:pxf
 * @data:2024/03/01
 **/
@SpringBootApplication
@MapperScan
public class CountingApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        System.out.println();
        SpringApplication.run(CountingApplication.class, args);
        SentinelTest.initFlowQpsRule();
    }
}