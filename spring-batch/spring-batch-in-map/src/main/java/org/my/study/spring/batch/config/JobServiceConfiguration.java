package org.my.study.spring.batch.config;

import org.my.study.spring.batch.service.InMemoryJobRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

@EnableBatchProcessing
@Configuration
@Import({
        MainBatchConfiguration.class
        /*DbBatchConfiguration*/})
public class JobServiceConfiguration {

    private static final String OVERRIDDEN_BY_EXPRESSION = null;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public InMemoryJobRepository<String> memoryJobRepository() {
        return new InMemoryJobRepository<>();
    }

    @Bean
    public JobFactory jobFactory() {
        return new ReferenceJobFactory(importJob());
    }

    @Bean
    public Job importJob() {
        return jobBuilderFactory.get("importJob")
                .start(step())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> reader(@Value("#{jobParameters[token]}") String jobToken) {
        List<String> data = StringUtils.isEmpty(jobToken)
                ? Collections.emptyList()
                : (List<String>) memoryJobRepository().get(jobToken);

        return new ListItemReader<>(data);
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                .<String, String>chunk(1)
                .reader(reader(OVERRIDDEN_BY_EXPRESSION))
                .writer(writer(OVERRIDDEN_BY_EXPRESSION))
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<String> writer(@Value("#{stepExecution.jobExecution.id}") String jobId) {
        System.out.println(jobId);
        return record -> {
            try {
                Thread.sleep(400);
            } catch (InterruptedException ignored) {
            }

            System.out.println(format("Thread: %d %s, \trecord:%s",
                    Thread.currentThread().getId(),
                    Thread.currentThread().getName(),
                    record));
        };
    }

    /*@Bean
    public ItemProcessor<Person, Person> processor() {
        return new PersonItemProcessor();
    }*/

/*    @Bean
    public ItemWriter<? super Object> writer() {
        return new ItemWriter<Object>() {
            @Override
            public void write(List<? extends Object> items) throws Exception {
                System.out.println(items);
            }
        };
    }*/
}