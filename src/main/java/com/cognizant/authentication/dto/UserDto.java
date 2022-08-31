package com.cognizant.authentication.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @NoArgsConstructor class UserDto {

	private String username;
	private String password;

	public UserDto(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

}
