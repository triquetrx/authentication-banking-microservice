package com.cognizant.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class PasswordChangeDTO {
	
	private String oldPassword;
	private String newPassword;

}
