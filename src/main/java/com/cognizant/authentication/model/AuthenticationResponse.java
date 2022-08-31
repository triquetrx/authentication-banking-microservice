package com.cognizant.authentication.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class AuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 7725147745848848020L;
	
	private String token;
	private String name;

}
