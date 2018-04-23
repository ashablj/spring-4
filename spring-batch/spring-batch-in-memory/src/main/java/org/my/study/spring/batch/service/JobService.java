package org.my.study.spring.batch.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
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

    @Autowired
    private JobExplorer jobExplorer;

    public String run(long maxCount) {
        String jobToken = UUID.randomUUID().toString();

        memoryJobRepository.put(jobToken, generateValues(maxCount, 10000));

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("token", jobToken)
                .addLong("value", new Random().nextLong())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            JobExecution run = jobLauncher.run(importJob, jobParameters);
//            jobLauncher.run(importJob.createJob(), jobParameters);
            return run.getId().toString();

        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public String stop(long id) {
        boolean success = false;
        try {
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

        String result = success ? "successfully" : "failure";
        logger.info(format("stopped job instance id: %d was %s", id, result));

        return result;
    }

    public String restart(Long id) {
        Long newId = -1L;

        try {
            newId = jobOperator.restart(id);
        } catch (Exception e) {
            logger.error(e);
        }

        logger.info(format("restart job instance id: %d set new %d", id, newId));
        return newId.toString();
    }

    public String shutdown(Long jobExecutionId) {
        Long newId = -1L;
        try {
            String executions = jobOperator.getSummary(jobExecutionId);
            JobExecution jobExecution = jobExplorer.getJobExecution(Long.valueOf(jobExecutionId));

            final BatchStatus status = jobExecution.getStatus();

            if (!BatchStatus.STOPPED.equals(status)) {
                stop(jobExecutionId);
            }

            newId = jobOperator.abandon(jobExecutionId).getId();
        } catch (Exception e) {
            logger.error(e);
        }

        logger.info(format("shutdown job instance id: %d", jobExecutionId));
        return newId.toString();
    }

    private Collection<String> generateValues(long id, int value) {
        return LongStream.range(0, id)
                .boxed()
                .map((i) -> String.valueOf(ThreadLocalRandom.current().nextInt(value)))
                .collect(toList());
    }
}