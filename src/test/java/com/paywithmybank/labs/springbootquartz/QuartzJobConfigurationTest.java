package com.paywithmybank.labs.springbootquartz;

import com.paywithmybank.labs.springbootquartz.configuration.QuartzJob;
import com.paywithmybank.labs.springbootquartz.configuration.QuartzJobConfiguration;
import com.paywithmybank.labs.springbootquartz.jobs.DynamicHelloWorldJob;
import com.paywithmybank.labs.springbootquartz.jobs.HelloWorldJob;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuartzJobConfigurationTest {

    @Mock
    private Scheduler scheduler;

    @Mock
    private Environment environment;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private QuartzJobConfiguration configuration;

    @Test
    void testQuartzCronScheduler() throws SchedulerException {
        // Given
        Job job = new HelloWorldJob();
        when(applicationContext.getBeansWithAnnotation(eq(QuartzJob.class)))
                .thenReturn(Collections.singletonMap("HelloWorldJob", job));

        // When
        configuration.scheduleJobs();

        // Then
        verify(scheduler).scheduleJob(any(), any());
    }

    @Test
    void testQuartzCronSpringElScheduler() throws SchedulerException {
        // Given
        Job job = new DynamicHelloWorldJob();
        when(applicationContext.getBeansWithAnnotation(eq(QuartzJob.class)))
                .thenReturn(Collections.singletonMap("DynamicHelloWorldJob", job));
                when(environment.getProperty(any(String.class)))
                        .thenReturn("0 0 0 * * ?");

        // When
        configuration.scheduleJobs();

        // Then
        verify(scheduler).scheduleJob(any(), any());
    }

    @Test
    void testQuartzInvalidCronScheduler() {
        // Given
        Job job = new DynamicHelloWorldJob();
        when(applicationContext.getBeansWithAnnotation(eq(QuartzJob.class)))
                .thenReturn(Collections.singletonMap("DynamicHelloWorldJob", job));
        when(environment.getProperty(any(String.class)))
                .thenReturn("abc 0 0 * * ?");

        Assertions.assertThrows(RuntimeException.class, () -> configuration.scheduleJobs());
    }

    @Test
    void testQuartzEmptyCronScheduler() {
        // Given
        Job job = new DynamicHelloWorldJob();
        when(applicationContext.getBeansWithAnnotation(eq(QuartzJob.class)))
                .thenReturn(Collections.singletonMap("DynamicHelloWorldJob", job));
        when(environment.getProperty(any(String.class)))
                .thenReturn("");

        Assertions.assertThrows(RuntimeException.class, () -> configuration.scheduleJobs());
    }

}
