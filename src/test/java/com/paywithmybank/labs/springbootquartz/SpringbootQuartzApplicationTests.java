package com.paywithmybank.labs.springbootquartz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import lombok.AllArgsConstructor;

@SpringBootTest
@AllArgsConstructor
class SpringbootQuartzApplicationTests {

    private final ApplicationContext applicationContext;

	@Test
	void contextLoads() {
        Assertions.assertNotNull(applicationContext);
	}

}
