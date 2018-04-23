package org.my.study.spring.batch.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MainBatchConfiguration {

    public static final int CORE_POOL_SIZE = 3;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean repositoryFactoryBean = new JobRepositoryFactoryBean();
        repositoryFactoryBean.setDatabaseType("mysql");
        repositoryFactoryBean.setDataSource(dataSource);
        repositoryFactoryBean.setTransactionManager(transactionManager);
        return repositoryFactoryBean.getObject();
    }

    @Bean
    public TaskExecutor customExecutorService() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setThreadNamePrefix("worker-");
        return taskExecutor;
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        jobLauncher.setTaskExecutor(customExecutorService());
        return jobLauncher;
    }

    /// -------------------- additional config -----------------------------------------------------

    @Bean
    public JobBuilderFactory jobBuilderFactory() throws Exception {
        return new JobBuilderFactory(jobRepository());
    }

    @Bean
    public StepBuilderFactory stepBuilderFactory() throws Exception {
        return new StepBuilderFactory(jobRepository(), transactionManager);
    }

    /// -------------------------------------------------------------------------------------------------------------

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    @Bean
    public JobExplorer jobExplorer() throws Exception {
        JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
        jobExplorerFactoryBean.setDataSource(dataSource);
        jobExplorerFactoryBean.afterPropertiesSet();
        return jobExplorerFactoryBean.getObject();
    }

    @Bean
    public JobOperator jobOperator() throws Exception {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer());
        jobOperator.setJobLauncher(jobLauncher());
        jobOperator.setJobRegistry(jobRegistry());
        jobOperator.setJobRepository(jobRepository());
        return jobOperator;
    }

    //    This bean post processors doesn't operate on the job bean because is not defined here, so it's moved to the BatchConfiguration java file
//    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}