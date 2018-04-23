package org.my.study.spring.web.controller;

import org.apache.commons.lang3.EnumUtils;
import org.my.study.spring.batch.enums.JobCommand;
import org.my.study.spring.batch.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static java.lang.String.format;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @RequestMapping(method = RequestMethod.GET)
    String test(HttpSession session) {
        UUID uid = (UUID) session.getAttribute("uid");
        if (uid == null) {
            uid = UUID.randomUUID();
        }
        session.setAttribute("uid", uid);
        return format("<<------------- id: %s \tTest: OK", uid);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{id}")
    public String runJob(@PathVariable Long id,
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
                return "START";

            case RESTART:
                jobService.restart(id);
                return "RESTART";

            case SHUTDOWN:
                jobService.shutdown(id);
                return "SHUTDOWN";

            default:
                return "UNKNOWN";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception ex) {
        return ex.getMessage();

    }
}

