package org.my.study.spring.batch.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MainJobOperator {

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job importJob;

    public static void main(String... args) throws Exception {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JobServiceConfiguration.class);

        MainJobOperator main = context.getBean(MainJobOperator.class);
        long executionId = main.jobOperator.start(main.importJob.getName(), null);

//        MainHelper.reportResults(main.jobOperator, executionId);
//        MainHelper.reportPeople(context.getBean(JdbcTemplate.class));

        context.close();

        System.out.printf("\nFIN %s", main.getClass().getName());

    }
}