package com.cognizant.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cognizant.authentication.controller.AuthenticationController;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ControllerTest {

	AuthenticationController controller;
	
	@Test
	void testController() {
		assertThat(controller).isNull();
	}
	
}
