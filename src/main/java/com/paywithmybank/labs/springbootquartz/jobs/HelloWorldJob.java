package com.paywithmybank.labs.springbootquartz.jobs;

import com.paywithmybank.labs.springbootquartz.configuration.QuartzJob;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@QuartzJob(name = "HelloWorld", cronExpression = "0/5 * * * * ?")
public class HelloWorldJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("Hello World from %s".formatted(this.getClass().getSimpleName()));
    }

}
