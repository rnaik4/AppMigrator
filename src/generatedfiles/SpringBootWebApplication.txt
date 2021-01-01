package com.hack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;


/**
 * A JCodeModel example.
 * 
 */
@SpringBootApplication
public class SpringBootWebApplication extends SpringBootServletInitializer
{
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

}
