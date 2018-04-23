package org.my.study.spring.batch.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.my.study.spring.batch.domain.repository.JobDataRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
public class JobService {

    private static final Log logger = LogFactory.getLog(JobService.class);
    public static final int SLEEP_MILLIS = 200;

    @Autowired
    private JobDataRepository<Collection<String>> dataRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importJob;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private JobExplorer jobExplorer;

    public String run(long maxCount) {
        String jobToken = UUID.randomUUID().toString();

        dataRepository.put(jobToken, generateValues(maxCount, 10000));

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("token", jobToken)
                .addLong("value", new Random().nextLong())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(importJob, jobParameters);
//            jobLauncher.run(importJob.createJob(), jobParameters);
            System.out.println(format("\n------------------> New created jobId: %d \texecutionId: %d", jobExecution.getJobId(), jobExecution.getId()));
            return jobExecution.getId().toString();

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

            success = jobOperator.stop(id);

            /*
            Set<Long> runningExecutions = jobOperator.getRunningExecutions(JOB_NAME);
            if (runningExecutions.iterator().hasNext()) {
                jobOperator.stop(runningExecutions.iterator().next());
            }*/
        } catch (Exception e) {
            logger.error(e);
        }

        String result = success ? "successfully" : "failure";
        logger.info(format("stopped job instance id: %d was %s", id, result));

        return result;
    }

    public String restart(Long executionId) {
        Long newId = -1L;

        /*jobExplorer.getJobExecution()
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
        jobExecution.setStartTime(new Date());
        jobExecution.setStatus(BatchStatus.STOPPED);
        jobExecution.incrementVersion();
        jobExecution.setLastUpdated(new Date(System.currentTimeMillis()));
        */

        BatchStatus actualJobStatus = jobExplorer.getJobExecution(executionId).getStatus();
        if (actualJobStatus != null && BatchStatus.STARTED.equals(actualJobStatus)) {
            stop(executionId);
        }

        try {
            Thread.sleep(SLEEP_MILLIS);
            newId = jobOperator.restart(executionId);
        } catch (Exception e) {
            logger.error(e);
        }

        logger.info(format("restart job instance id: %d set new %d", executionId, newId));
        return newId.toString();
    }

    public String shutdown(Long jobExecutionId) {
        Long newId = -1L;
        try {
            String executions = jobOperator.getSummary(jobExecutionId);
            JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);

            final BatchStatus status = jobExecution.getStatus();

            if (!BatchStatus.STOPPED.equals(status)) {
                stop(jobExecutionId);
            }

            Thread.sleep(200);
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