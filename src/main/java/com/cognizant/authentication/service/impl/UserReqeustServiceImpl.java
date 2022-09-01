package com.cognizant.authentication.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cognizant.authentication.dto.ConfirmPasswordDTO;
import com.cognizant.authentication.dto.NewUserDTO;
import com.cognizant.authentication.dto.PasswordChangeDTO;
import com.cognizant.authentication.exception.PasswordNotAMatchException;
import com.cognizant.authentication.model.Users;
import com.cognizant.authentication.repository.UserRepository;
import com.cognizant.authentication.service.UserRequestService;

@Service
public class UserReqeustServiceImpl implements UserRequestService  {

	@Autowired
	UserRepository repository;

	@Override
	@Transactional
	public void newUser(NewUserDTO newUserDTO) {

		int id = (int) repository.count() + 1;
		repository.save(new Users(id, newUserDTO.getCustomerId(), newUserDTO.getName(), newUserDTO.getUsername(),
				encoder().encode(newUserDTO.getPassword()), "ROLE_USER"));
	}
	
	@Override
	@Transactional
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
	
	@Override
	@Transactional
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
