package com.cognizant.componentprocessing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.componentprocessing.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

	Optional<Users> findByUsername(String userName);
	
	
}
