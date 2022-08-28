package com.cognizant.componentprocessing.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.componentprocessing.dto.ConfirmPasswordDTO;
import com.cognizant.componentprocessing.dto.NewUserDTO;
import com.cognizant.componentprocessing.dto.PasswordChangeDTO;
import com.cognizant.componentprocessing.dto.ValidatingDTO;
import com.cognizant.componentprocessing.exception.PasswordNotAMatchException;
import com.cognizant.componentprocessing.model.AuthenticationRequest;
import com.cognizant.componentprocessing.model.AuthenticationResponse;
import com.cognizant.componentprocessing.service.JwtUserDetailsService;
import com.cognizant.componentprocessing.service.RegisterUserService;
import com.cognizant.componentprocessing.util.JwtTokenUtil;

@RestController
public class AuthenticationController {

	@Autowired
	AuthenticationManager authentication;

	@Autowired
	JwtTokenUtil jwt;

	@Autowired
	JwtUserDetailsService userDetails;

	@Autowired
	RegisterUserService registerService;

	private ValidatingDTO dto = new ValidatingDTO();

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthentication(@RequestBody AuthenticationRequest request) throws Exception {
		authenticate(request.getUsername(), request.getPassword());
		final UserDetails userRequest = userDetails.loadUserByUsername(request.getUsername());
		final String token = jwt.generateToken(userRequest);
		try {
			return ResponseEntity.ok(new AuthenticationResponse(token, userDetails.getName(userRequest.getUsername())));
		} catch (Exception e) {
			return (ResponseEntity<?>) ResponseEntity.status(400);
		}
	}

	@GetMapping("/validate")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<?> validatingToken(@RequestHeader(name = "Authorization") String token) {

		String tokenDup = token.substring(7);
		try {
			UserDetails user = userDetails.loadUserByUsername(jwt.getUsernameFromToken(tokenDup));
			String role = user.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(","));
			if (jwt.validateToken(tokenDup, user)) {
				dto.setValidStatus(true);
				dto.setUserRole(role);
				dto.setCustomerId(userDetails.getCustomerId(user.getUsername()));
				return new ResponseEntity<>(dto, HttpStatus.OK);
			}
			dto.setValidStatus(false);
			return new ResponseEntity<>(dto, HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			dto.setValidStatus(false);
			return new ResponseEntity<>(dto, HttpStatus.FORBIDDEN);
		}

	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authentication.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/register")
	public ResponseEntity<?> addNewUser(@RequestBody NewUserDTO newUserDTO) {
		registerService.newUser(newUserDTO);
		return ResponseEntity.ok("New User Created");
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestHeader(name = "Authorization") String token,
			@RequestBody PasswordChangeDTO dto) {
		String tokenDup = token.substring(7);
		try {
			UserDetails user = userDetails.loadUserByUsername(jwt.getUsernameFromToken(tokenDup));
			if (jwt.validateToken(tokenDup, user)) {
				String result = registerService.changePassword(user.getUsername(), dto);
				return new ResponseEntity<>(result, HttpStatus.OK);
			}
			return new ResponseEntity<>("UNAUTHORIZED_ACCESS", HttpStatus.FORBIDDEN);
		} catch (PasswordNotAMatchException e) {
			return new ResponseEntity<>("PASSWORD_NOT_A_MATCH", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("UNAUTHORIZED_ENTRY", HttpStatus.FORBIDDEN);
		}
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/check-password")
	public ResponseEntity<?> chechPassword(@RequestHeader(name = "Authorization") String token,
			@RequestBody ConfirmPasswordDTO confirmPasswordDTO) {
		String tokenDup = token.substring(7);
		try {
			UserDetails user = userDetails.loadUserByUsername(jwt.getUsernameFromToken(tokenDup));
			if (jwt.validateToken(tokenDup, user)) {
				boolean result = registerService.checkPassword(user.getUsername(), confirmPasswordDTO);
				return new ResponseEntity<>(result, HttpStatus.OK);
			}
			return new ResponseEntity<>("UNAUTHORIZED_ACCESS", HttpStatus.BAD_REQUEST);
		} catch (PasswordNotAMatchException e) {
			return new ResponseEntity<>("PASSWORD_NOT_A_MATCH", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("UNAUTHORIZED_ENTRY", HttpStatus.FORBIDDEN);
		}
	}
}
