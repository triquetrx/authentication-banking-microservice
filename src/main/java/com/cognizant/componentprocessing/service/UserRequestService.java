package com.cognizant.componentprocessing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cognizant.componentprocessing.dto.ConfirmPasswordDTO;
import com.cognizant.componentprocessing.dto.NewUserDTO;
import com.cognizant.componentprocessing.dto.PasswordChangeDTO;
import com.cognizant.componentprocessing.exception.PasswordNotAMatchException;
import com.cognizant.componentprocessing.model.Users;
import com.cognizant.componentprocessing.repository.UserRepository;

@Service
public class UserRequestService {

	@Autowired
	UserRepository repository;

	public void newUser(NewUserDTO newUserDTO) {

		int id = (int) repository.count() + 1;
		repository.save(new Users(id, newUserDTO.getCustomerId(), newUserDTO.getName(), newUserDTO.getUsername(),
				encoder().encode(newUserDTO.getPassword()), "ROLE_USER"));
	}
	
	public String changePassword(String username,PasswordChangeDTO passwordChangeDTO) throws PasswordNotAMatchException {
		Users users = repository.findByUsername(username).get();
		if(encoder().matches(passwordChangeDTO.getOldPassword(), users.getPassword())) {
			users.setPassword(encoder().encode(passwordChangeDTO.getNewPassword()));
			repository.save(users);
			return "CHANGED_PASSWORD_SUCCESSFULLY";
		} else {			
			throw new PasswordNotAMatchException();
		}
	}
	
	public boolean checkPassword(String username,ConfirmPasswordDTO dto) throws PasswordNotAMatchException {
		Users users = repository.findByUsername(username).get();
		if(encoder().matches(dto.getConfirmPassword(), users.getPassword())) {
			return true;
		} else {			
			throw new PasswordNotAMatchException();
		}
	}

	private PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

}
