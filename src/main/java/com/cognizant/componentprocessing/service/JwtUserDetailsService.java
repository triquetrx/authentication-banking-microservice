package com.cognizant.componentprocessing.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cognizant.componentprocessing.model.Users;
import com.cognizant.componentprocessing.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepo;
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Users> users = userRepo.findByUsername(username);
		users.orElseThrow(()->new UsernameNotFoundException("User Not found"));
		return users.map(LocalUserDetails::new).get();
	}

	@Override
	public String toString() {
		return "JwtUserDetailsService [userRepo=" + userRepo + ", log=" + log + "]";
	}
	
	
	public String getName(String username) {
		Users users = userRepo.findByUsername(username).get();
		return users.getName();
	}
	
	public String getCustomerId(String username) {
		Users users = userRepo.findByUsername(username).get();
		return users.getCustomerId();
	}
	

}
