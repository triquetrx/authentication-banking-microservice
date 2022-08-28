package com.cognizant.componentprocessing.model;

import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @NoArgsConstructor class AuthenticationRequest {

	private String username;
	private String password;

	public AuthenticationRequest(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

}
