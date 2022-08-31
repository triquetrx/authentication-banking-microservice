package com.cognizant.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class NewUserDTO {

	private String name;
	private String username;
	private String password;
	private String customerId;
	
}
