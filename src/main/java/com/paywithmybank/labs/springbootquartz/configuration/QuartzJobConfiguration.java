package com.paywithmybank.labs.springbootquartz.configuration;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
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
import org.springframework.core.env.Environment;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.text.ParseException;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
public class QuartzJobConfiguration {

    private static final int CRON_EXPRESSION_OFFSET_START = 2;
    private static final int CRON_EXPRESSION_OFFSET_END = 1;
    private static final String CRON_EXPRESSION_PREFIX = "${";
    private static final String CRON_EXPRESSION_SUFFIX = "}";
    private static final int SPRING_EL_PARTS = 2;
    private static final int SPRING_EL_PART_EXPRESSION = 0;
    private static final int SPRING_EL_PART_DEFAULT = 1;
    private static final String SPRING_EL_PARTS_SEPARATOR = ":";

    private final Scheduler scheduler;

    private final Environment environment;

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void scheduleJobs() {
        log.info("Scheduling jobs ...");

        Map<String, Object> jobs = applicationContext.getBeansWithAnnotation(QuartzJob.class);

        jobs.values().forEach(job -> processJob((Job) job));

    }

    private void processJob(final Job job) {
        try {
            var annotation = job.getClass().getAnnotation(QuartzJob.class);

            var jobClass = job.getClass();
            var jobName = (annotation.name().isBlank()) ? job.getClass().getSimpleName() : annotation.name();
            var triggerName = "%s_TRIGGER".formatted(jobName.toUpperCase());
            var jobCronExpression = processCronExpression(annotation.cronExpression());

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
    }

    private String processCronExpression(final String cronExpression) {
        if (StringUtils.isBlank(cronExpression)) {
            throw new IllegalArgumentException("cronExpression cannot be null, empty or blank");
        }

        String cronValue = null;
        if (isValidSpringElExpression(cronExpression)) {
            String cronExpressionValue = extractSringElExpression(cronExpression);
            cronValue = environment.getProperty(cronExpressionValue);
        } else {
            cronValue = cronExpression;
        }

        validateCronExpression(cronValue);

        return cronValue;
    }

    private boolean isValidSpringElExpression(final String cronExpression) {
        boolean isValid = false;

        if (cronExpression.startsWith(CRON_EXPRESSION_PREFIX) && cronExpression.endsWith(CRON_EXPRESSION_SUFFIX)) {
            String expressionValue = extractSringElExpression(cronExpression);
            try {
                ExpressionParser parser = new SpelExpressionParser();
                parser.parseExpression(expressionValue);
                isValid = true;
            } catch (Exception e) {
                log.error("The cronExpression '{}' is not valid", cronExpression, e);
            }
        }

        return isValid;
    }

    private String extractSringElExpression(final String cronExpression) {
        String expression = cronExpression.substring(CRON_EXPRESSION_OFFSET_START, cronExpression.length() - CRON_EXPRESSION_OFFSET_END); // Strip ${ and }
        String expressionValue = (expression.contains(SPRING_EL_PARTS_SEPARATOR)) ? expression.split(SPRING_EL_PARTS_SEPARATOR, SPRING_EL_PARTS)[SPRING_EL_PART_EXPRESSION] : expression;
        return expressionValue;
    }

    private void validateCronExpression(final String cronValue) {
        if (StringUtils.isBlank(cronValue)) {
            throw new IllegalArgumentException("cronValue cannot be null, empty or blank");
        }

        try {
            new CronExpression(cronValue);
        } catch (ParseException e) {
            throw new RuntimeException("CRON expression '%s' is invalid".formatted(cronValue), e);
        }
    }

}
