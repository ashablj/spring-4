package org.my.study.spring.batch.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MainBatchConfiguration {

    @Bean
    public MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean() {
        return new MapJobRepositoryFactoryBean(transactionManager());
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public JobRepository jobRepository() {
       /* JobRepositoryFactoryBean repositoryFactoryBean = new JobRepositoryFactoryBean();
        repositoryFactoryBean.setDatabaseType("h2");
        repositoryFactoryBean.setDataSource(dataSource());
        try {
            return repositoryFactoryBean.getObject();
        } catch (Exception ignored) {
        }
*/
        // map repository
        try {
            return mapJobRepositoryFactoryBean().getObject();
        } catch (Exception ignored) {
        }
        return null;
    }

    @Bean
    public TaskExecutor customExecutorService() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setThreadNamePrefix("worker-");
        return taskExecutor;
    }

    @Bean
    public JobLauncher jobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        jobLauncher.setTaskExecutor(customExecutorService());
//        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor(new ThreadFactory().));
        return jobLauncher;
    }

    /// -------------------- additional config -----------------------------------------------------

    @Bean
    public JobBuilderFactory jobBuilderFactory() throws Exception {
        return new JobBuilderFactory(jobRepository());
    }

    @Bean
    public StepBuilderFactory stepBuilderFactory() {
        return new StepBuilderFactory(jobRepository(), transactionManager());
    }

    /// -------------------------------------------------------------------------------------------------------------

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    @Bean
    public JobExplorer jobExplorer() {
        try {
            return new MapJobExplorerFactoryBean(mapJobRepositoryFactoryBean()).getObject();

        } catch (Exception ignored) {
        }
        return null;
    }

    @Bean
    public JobOperator jobOperator() {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer());
        jobOperator.setJobLauncher(jobLauncher());
        jobOperator.setJobRegistry(jobRegistry());
        jobOperator.setJobRepository(jobRepository());
        return jobOperator;
    }

    /// -------------------------------------------------------------------------------------------------------------

//        MapJobExplorerFactoryBean jobExplorerFactoryBean = new MapJobExplorerFactoryBean();//JobExplorerFactoryBean();
////        jobExplorerFactoryBean.setDataSource(dataSource());
//        jobExplorerFactoryBean.setRepositoryFactory(new MapJobRepositoryFactoryBean(transactionManager()));
//        try {
//            return jobExplorerFactoryBean.getObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }

    //    This bean post processors doesn't operate on the job bean because is not defined here, so it's moved to the BatchConfiguration java file
//    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    //    Not enough knowledge from the time being: the application context factory tries to create the application factory resulting in two possible errors:
//    · With new GenericApplicationContextFactory(ApplicationConfiguration.class.getPackage().getName()); Error creating bean with name 'additionalBatchConfiguration': Failed to execute SQL script statement at line 1 of resource class path resource [org/springframework/batch/core/schema-hsqldb.sql]: CREATE TABLE BATCH_JOB_INSTANCE ( JOB_INSTANCE_ID BIGINT IDENTITY NOT NULL PRIMARY KEY , VERSION BIGINT , JOB_NAME VARCHAR(100) NOT NULL, JOB_KEY VARCHAR(32) NOT NULL, constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY) ) ; nested exception is java.sql.SQLSyntaxErrorException: object name already exists: BATCH_JOB_INSTANCE in statement [CREATE TABLE BATCH_JOB_INSTANCE...
//    · With new GenericApplicationContextFactory(BatchConfiguration.class); Error creating bean with name 'importUserJob': No qualifying bean of type [javax.sql.DataSource] found for dependency: expected at least 1 bean
/*    @Bean
    public AutomaticJobRegistrar automaticJobRegistrar() {
        AutomaticJobRegistrar automaticJobRegistrar = new AutomaticJobRegistrar();
        GenericApplicationContextFactory genericApplicationContextFactory = new GenericApplicationContextFactory(BatchConfiguration.class);
        ApplicationContextFactory[] applicationContextFactories = {genericApplicationContextFactory};
        automaticJobRegistrar.setApplicationContextFactories(applicationContextFactories);
        DefaultJobLoader defaultJobLoader = new DefaultJobLoader(jobRegistry());
        automaticJobRegistrar.setJobLoader(defaultJobLoader);
        return automaticJobRegistrar;
    }*/
}