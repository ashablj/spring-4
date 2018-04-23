package org.my.study.spring.web.controller;

import org.apache.commons.lang3.EnumUtils;
import org.my.study.spring.batch.enums.JobCommand;
import org.my.study.spring.batch.service.JobService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception ex) {
        return ex.getMessage();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/create/{maxCount}")
    public String create(@PathVariable Long maxCount) {
        Assert.notNull(maxCount, "Max count can't be Null value.");

        String result = jobService.run(maxCount);
        return format("<<------------- The job with execution id:%s, max count:%s was %s", result, maxCount, BatchStatus.STARTING);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/stop/{id}")
    public String stop(@PathVariable Long id) {
        Assert.notNull(id, "The execution id can't be Null value.");

        String result = jobService.stop(id);
        return format("<<------------- The job with execution id:%s command:%s was %s", id, BatchStatus.STOPPED, result);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/resume/{id}")
    public String resume(@PathVariable Long id) {
        Assert.notNull(id, "The execution id can't be Null value.");

        String result = jobService.restart(id);
        return format("<<------------- The job with execution id:%s command:%s new id:%s", id, BatchStatus.STARTED, result);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/shutdown/{id}")
    public String shutdown(@PathVariable Long id) {
        Assert.notNull(id, "The execution id can't be Null value.");

        String result = jobService.shutdown(id);
        return format("<<------------- The job with execution id:%s command:%s new id:%s", id, BatchStatus.ABANDONED, result);
    }

    /// -------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.PUT, value = "/execute/{id}")
    public String doJob(@PathVariable Long id,
                        @RequestBody String command) {

        Assert.notNull(id, "Job ID can't be Null value.");
        Assert.notNull(command, "Job COMMAND can't be Null value.");
        Assert.isTrue(EnumUtils.isValidEnum(JobCommand.class, command.toUpperCase()), format("The JOB COMMAND '%s' was not recognized", command));

        String result = executeCommand(EnumUtils.getEnum(JobCommand.class, command.toUpperCase()), id);
        return format("<<------------- job id:%s, execute command:%s was done!", id, result);
    }

    private String executeCommand(JobCommand command, Long id) {
        switch (command) {
            case RUN:
                jobService.run(id);
                return "RUN";

            case STOP:
                jobService.stop(id);
                return "STOP";

            case START:
                jobService.restart(id);
                return "RESTART";

            case SHUTDOWN:
                jobService.shutdown(id);
                return "SHUTDOWN";

            default:
                return "UNKNOWN";
        }
    }
}

