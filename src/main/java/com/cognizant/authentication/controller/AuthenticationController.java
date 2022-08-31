package com.cognizant.authentication.controller;

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

import com.cognizant.authentication.dto.ConfirmPasswordDTO;
import com.cognizant.authentication.dto.NewUserDTO;
import com.cognizant.authentication.dto.PasswordChangeDTO;
import com.cognizant.authentication.dto.ValidatingDTO;
import com.cognizant.authentication.exception.PasswordNotAMatchException;
import com.cognizant.authentication.model.AuthenticationRequest;
import com.cognizant.authentication.model.AuthenticationResponse;
import com.cognizant.authentication.service.JwtUserDetailsService;
import com.cognizant.authentication.service.UserRequestService;
import com.cognizant.authentication.util.JwtTokenUtil;

@RestController
public class AuthenticationController {

	@Autowired
	AuthenticationManager authentication;

	@Autowired
	JwtTokenUtil jwt;

	@Autowired
	JwtUserDetailsService userDetails;

	@Autowired
	UserRequestService requestService;

	private ValidatingDTO dto = new ValidatingDTO();

	@CrossOrigin(origins = "http://localhost:5000")
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthentication(@RequestBody AuthenticationRequest request) {
		try {
			authenticate(request.getUsername(), request.getPassword());
			final UserDetails userRequest = userDetails.loadUserByUsername(request.getUsername());
			final String token = jwt.generateToken(userRequest);
			return ResponseEntity.ok(new AuthenticationResponse(token, userDetails.getName(userRequest.getUsername())));
		} catch (DisabledException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/validate")
	@CrossOrigin(origins = "http://localhost:5000")
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
			return new ResponseEntity<>("TOKEN_INVALID_OR_EXPIRED", HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			return new ResponseEntity<>("INVALID_TOKEN", HttpStatus.FORBIDDEN);
		}
	}

	private void authenticate(String username, String password) throws DisabledException, BadCredentialsException {
		try {
			authentication.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new DisabledException("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("INVALID_CREDENTIALS", e);
		}
	}

	@CrossOrigin(origins = "http://localhost:5000")
	@PostMapping("/register")
	public ResponseEntity<?> addNewUser(@RequestBody NewUserDTO newUserDTO) {
		requestService.newUser(newUserDTO);
		return ResponseEntity.ok("New User Created");
	}

	@CrossOrigin(origins = "http://localhost:5000")
	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestHeader(name = "Authorization") String token,
			@RequestBody PasswordChangeDTO dto) {
		String tokenDup = token.substring(7);
		try {
			UserDetails user = userDetails.loadUserByUsername(jwt.getUsernameFromToken(tokenDup));
			if (jwt.validateToken(tokenDup, user)) {
				String result = requestService.changePassword(user.getUsername(), dto);
				return new ResponseEntity<>(result, HttpStatus.OK);
			}
			return new ResponseEntity<>("UNAUTHORIZED_ACCESS", HttpStatus.FORBIDDEN);
		} catch (PasswordNotAMatchException e) {
			return new ResponseEntity<>("PASSWORD_NOT_A_MATCH", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("UNAUTHORIZED_ENTRY", HttpStatus.FORBIDDEN);
		}
	}

	@CrossOrigin(origins = "http://localhost:5000")
	@PostMapping("/check-password")
	public ResponseEntity<?> chechPassword(@RequestHeader(name = "Authorization") String token,
			@RequestBody ConfirmPasswordDTO confirmPasswordDTO) {
		String tokenDup = token.substring(7);
		try {
			UserDetails user = userDetails.loadUserByUsername(jwt.getUsernameFromToken(tokenDup));
			if (jwt.validateToken(tokenDup, user)) {
				boolean result = requestService.checkPassword(user.getUsername(), confirmPasswordDTO);
				return new ResponseEntity<>(result, HttpStatus.OK);
			}
			return new ResponseEntity<>("UNAUTHORIZED_ACCESS", HttpStatus.FORBIDDEN);
		} catch (PasswordNotAMatchException e) {
			return new ResponseEntity<>("PASSWORD_NOT_A_MATCH", HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			return new ResponseEntity<>("UNAUTHORIZED_ENTRY", HttpStatus.BAD_REQUEST);
		}
	}
}
