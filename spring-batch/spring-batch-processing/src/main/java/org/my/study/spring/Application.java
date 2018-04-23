package org.my.study.spring;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);

//        SpringApplication app = new SpringApplication(Application.class);
//        ConfigurableApplicationContext ctx= app.run(args);
//        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
    }
}
