package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @description:Application
 * @author:pxf
 * @data:2023/11/20
 **/
@SpringBootApplication
public class LeaveApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(LeaveApplication.class, args);
    }

}
