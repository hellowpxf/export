package tax;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

/**
 * @description:TaxApplication
 * @author:pxf
 * @data:2023/04/18
 **/
@EnableCaching
@SpringBootApplication
public class TaxApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(TaxApplication.class, args);
    }
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Bean
    public Redisson redisson(){
        Config config = new Config();
        System.out.println("+sssss+"+redisHost);
        config.useSingleServer()
                .setAddress("redis://"+redisHost+":"+redisPort)
                .setDatabase(0);
        return  (Redisson) Redisson.create(config);
    }
}

