package com.rest.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rest.api.Exception.UserServiceException;
import com.rest.api.Repository.UserRepository;
import com.rest.api.Util.UserUtil;
import com.rest.api.entity.UserEntity;
import com.rest.api.model.response.ErrorMessages;
import com.rest.api.service.UserService;
import com.rest.api.sharedClass.DTO.AddressDTO;
import com.rest.api.sharedClass.DTO.UserDTO;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDTO createUser(UserDTO userDTO) {

		if (userRepository.findByEmail(userDTO.getEmail()) != null)
			throw new RuntimeException("Record Already Exists");

		// BeanUtils.copyProperties(userDTO, userEntity);
		int i=0;
		for(AddressDTO address : userDTO.getAddresses()) {
			
			address.setUserDetails(userDTO);
			address.setAddressId(userUtil.generateAddressId(30));
			userDTO.getAddresses().set(i++, address);
		}
		
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);

		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
		String userId = userUtil.generateUserId(20);
		
		userEntity.setUserId(userId);
		UserEntity storedUser = userRepository.save(userEntity);
		
	//	UserDTO returnValue = new UserDTO();
		
		UserDTO returnValue = modelMapper.map(storedUser, UserDTO.class);
		
		//BeanUtils.copyProperties(storedUser, returnValue);
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDTO getUser(String email) {

		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(userEntity, userDTO);

		return userDTO;
	}

	@Override
	public UserDTO getUserByUserId(String userId) {

		UserDTO returnValue = new UserDTO();

		UserEntity userEntity = userRepository.findUserByUserId(userId);
		if (userEntity == null)

			throw new UsernameNotFoundException("user with id " + userId + " not found");
		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDTO updateUser(String id, UserDTO userDTO) {

		UserDTO returnValue = new UserDTO();

		UserEntity userEntity = userRepository.findUserByUserId(id);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}

		userEntity.setFirstName(userDTO.getFirstName());
		userEntity.setLastName(userDTO.getLastName());

		UserEntity updatedUserDetails = userRepository.save(userEntity);

		BeanUtils.copyProperties(updatedUserDetails, returnValue);

		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {

		UserEntity userEntity = userRepository.findUserByUserId(userId);

		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}
		userRepository.delete(userEntity);

	}

	@Override
	public List<UserDTO> getUsers(int page, int limit) {

		if (page > 0)
			page = page - 1;

		List<UserDTO> returnValue = new ArrayList<UserDTO>();

		Pageable pageableRequest = PageRequest.of(page, limit);

		Page<UserEntity> UsersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = UsersPage.getContent();

		for (UserEntity user : users) {

			UserDTO userDTO = new UserDTO();
			BeanUtils.copyProperties(user, userDTO);
			returnValue.add(userDTO);
		}
		return returnValue;
	}

}
