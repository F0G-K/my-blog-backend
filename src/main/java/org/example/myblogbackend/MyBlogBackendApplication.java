package org.example.myblogbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.myblogbackend.mapper")
public class MyBlogBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBlogBackendApplication.class, args);
    }

}
