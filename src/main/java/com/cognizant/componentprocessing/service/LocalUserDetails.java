package com.cognizant.componentprocessing.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cognizant.componentprocessing.model.Users;

public class LocalUserDetails implements UserDetails {

	private static final long serialVersionUID = 8970531752632402208L;
	private String username;
	private String password;
	private Collection<GrantedAuthority> roles;

	public LocalUserDetails(Users users) {
		super();
		this.username = users.getUsername();
		this.password = users.getPassword();
		this.roles = Arrays.stream(users.getRoles().split(",")).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}