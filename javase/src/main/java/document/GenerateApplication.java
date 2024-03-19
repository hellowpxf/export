package document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * @description:GenerateApplication
 * @author:pxf
 * @data:2023/03/18
 **/
@SpringBootApplication
public class GenerateApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        System.out.println();
        SpringApplication.run(GenerateApplication.class, args);
    }
}
