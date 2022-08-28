package com.cognizant.componentprocessing.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
public @Data @AllArgsConstructor @NoArgsConstructor class Users {

	@Id
	private Integer userId;
	private String customerId;
	private String name;
	private String username;
	private String password;
	private String roles;

}
