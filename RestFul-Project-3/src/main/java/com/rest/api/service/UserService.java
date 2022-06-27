package com.rest.api.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.rest.api.sharedClass.DTO.UserDTO;

public interface UserService extends UserDetailsService {

	public UserDTO createUser(UserDTO userDTO);
	
	public UserDTO getUser(String email);
	
	public UserDTO getUserByUserId(String userId);
	
	public UserDTO updateUser(String id ,UserDTO userDTO);
	
	public void deleteUser(String userId);
	
	public List<UserDTO> getUsers(int page, int limit);
}
