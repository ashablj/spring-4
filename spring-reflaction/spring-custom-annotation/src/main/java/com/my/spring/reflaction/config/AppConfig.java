package com.my.spring.reflaction.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan("com.my.spring.reflaction.service")
public class AppConfig {

    @PostConstruct
    public void doSomething() {
        System.out.println("Init configuration Done!");
    }
}
