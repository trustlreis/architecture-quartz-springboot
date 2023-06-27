package com.paywithmybank.labs.springbootquartz.jobs;

import com.paywithmybank.labs.springbootquartz.configuration.QuartzJob;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@QuartzJob(name = "HelloWorld", cronExpression = "0/5 * * * * ?")
@Component
public class HelloWorldJob implements org.quartz.Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Hello World");
    }

}
