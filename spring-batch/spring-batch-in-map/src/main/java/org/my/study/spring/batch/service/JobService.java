package org.my.study.spring.batch.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
public class JobService {

    private static final Log logger = LogFactory.getLog(JobService.class);
    private static final String JOB_NAME = "importJob";

    @Autowired
    private InMemoryJobRepository<String> memoryJobRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importJob;
//    private JobFactory importJob;

    @Autowired
    private JobOperator jobOperator;

    public void run(long id) {
        String jobToken = UUID.randomUUID().toString();

        memoryJobRepository.put(jobToken, generateValues(id, 10000));

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("token", jobToken)
                .addLong("id", id)
                .addLong("value", new Random().nextLong())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(importJob, jobParameters);
//            jobLauncher.run(importJob.createJob(), jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop(long id) {
        boolean success = false;
        try {
//            jobOperator.stop(id);
//            jobOperator.stop(jobExecution.getId());
//            jobLauncher.getId

            String result = jobOperator.getJobNames().toString();
            System.out.println("----------------------> getJobNames: " + result);

//            Map<Long, String> stepExecutionSummaries = jobOperator.getStepExecutionSummaries(2);
//            for (String stepExecutionSummary : stepExecutionSummaries.values()) {
//                sb.append("\n").append(stepExecutionSummary);
//            }

            Set<Long> runningExecutions = jobOperator.getRunningExecutions(JOB_NAME);
            success = jobOperator.stop(id);

            /*if (runningExecutions.iterator().hasNext()) {
                jobOperator.stop(runningExecutions.iterator().next());
            }*/


        } catch (Exception e) {
            logger.error(e);
        }
        logger.info(format("stopped job instance id: %d was %s", id, success ? "successfully" : "failure"));
    }

    public void restart(Long id) {
        long newId = 0;
        try {
            newId = jobOperator.restart(id);
        } catch (Exception e) {
            logger.error(e);
        }

        logger.info(format("restart job instance id: %d to %d", id, newId));
    }

    public void shutdown(Long id) {
        try {
            jobOperator.abandon(id);
        } catch (Exception e) {
            logger.error(e);
        }

        logger.info(format("restart job instance id: %d to %d", id, 0));
    }

    private Collection<String> generateValues(long id, int value) {
        return LongStream.range(0, id)
                .boxed()
                .map((i) -> String.valueOf(ThreadLocalRandom.current().nextInt(value)))
                .collect(toList());

    }
}