package com.paywithmybank.labs.springbootquartz.configuration;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
public class QuartzConfiguration {

    private final Scheduler scheduler;

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void scheduleJobs() {
        log.info("Scheduling jobs ...");

        Map<String, Object> jobs = applicationContext.getBeansWithAnnotation(QuartzJob.class);

        jobs.values().forEach(qj -> {
            try {
                var job = (Job) qj;
                var annotation = job.getClass().getAnnotation(QuartzJob.class);

                var jobClass = job.getClass();
                var jobName = annotation.name();
                var triggerName = "%s_TRIGGER".formatted(annotation.name().toUpperCase());
                var jobCronExpression = annotation.cronExpression();

                JobDetail jobDetail = JobBuilder.newJob(jobClass)
                        .withIdentity(jobName)
                        .storeDurably()
                        .build();

                CronTrigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerName)
                        .forJob(jobDetail)
                        .withSchedule(CronScheduleBuilder.cronSchedule(jobCronExpression))
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);

                log.info("Job '{}' ({}) was scheduled to {}", jobName, jobClass, jobCronExpression);
            } catch (SchedulerException se) {
                log.error(se.getMessage(), se);
            }
        });

    }

}
