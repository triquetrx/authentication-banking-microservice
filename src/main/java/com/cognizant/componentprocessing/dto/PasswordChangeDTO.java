package com.cognizant.componentprocessing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @AllArgsConstructor @NoArgsConstructor class PasswordChangeDTO {
	
	private String oldPassword;
	private String newPassword;

}
