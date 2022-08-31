package com.cognizant.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cognizant.authentication.service.UserRequestService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceTest {
	
	UserRequestService requestService;
	
	@Test
	void testReqeuestService() {
		assertThat(requestService).isNull();
	}

}
