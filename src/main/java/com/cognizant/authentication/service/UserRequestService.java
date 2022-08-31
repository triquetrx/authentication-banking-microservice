package com.cognizant.authentication.service;

import org.springframework.stereotype.Service;

import com.cognizant.authentication.dto.ConfirmPasswordDTO;
import com.cognizant.authentication.dto.NewUserDTO;
import com.cognizant.authentication.dto.PasswordChangeDTO;
import com.cognizant.authentication.exception.PasswordNotAMatchException;

@Service
public interface UserRequestService {

	void newUser(NewUserDTO newUserDTO);

	String changePassword(String username, PasswordChangeDTO passwordChangeDTO) throws PasswordNotAMatchException;

	boolean checkPassword(String username, ConfirmPasswordDTO dto) throws PasswordNotAMatchException;

}
