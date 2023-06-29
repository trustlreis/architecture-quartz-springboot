package com.paywithmybank.labs.springbootquartz;

import com.paywithmybank.labs.springbootquartz.jobs.HelloWorldJob;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@ExtendWith(MockitoExtension.class)
public class HelloWorldJobTest {

    @InjectMocks
    private HelloWorldJob job;

    @Mock
    private JobExecutionContext context;
    
    @Test
    public void testExecute() {
        Assertions.assertDoesNotThrow(() -> job.execute(context));
    }

}
