package com.rest.api.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rest.api.model.Request.UserDetailsRequest;
import com.rest.api.model.response.AddressResponse;
import com.rest.api.model.response.OperationStatusMOdel;
import com.rest.api.model.response.RequestOperationName;
import com.rest.api.model.response.RequestOperationStatus;
import com.rest.api.model.response.UserResponse;
import com.rest.api.service.AddressService;
import com.rest.api.service.UserService;
import com.rest.api.sharedClass.DTO.AddressDTO;
import com.rest.api.sharedClass.DTO.UserDTO;

@RestController
@RequestMapping("/users") // https://localhost:8080/users
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserResponse> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {

		List<UserResponse> returnValue = new ArrayList<>();

		List<UserDTO> users = userService.getUsers(page, limit);

		for (UserDTO userDTO : users) {

			UserResponse userResponse = new UserResponse();
			BeanUtils.copyProperties(userDTO, userResponse);
			returnValue.add(userResponse);
		}

		return returnValue;
	}

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserResponse getUser(@PathVariable String id) {

		UserResponse returnValue = new UserResponse();
		UserDTO userDTO = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDTO, returnValue);
		return returnValue;
	}

	@PostMapping(path = "/save", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public UserResponse createUser(@RequestBody UserDetailsRequest userDetails) throws Exception {
		
		UserResponse userResponse = new UserResponse();

		// UserDTO userDTO = new UserDTO();
		// BeanUtils.copyProperties(userDetails, userDTO);

		ModelMapper modelMapper = new ModelMapper();
		UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);

		UserDTO createdUser = userService.createUser(userDTO);

		// BeanUtils.copyProperties(createdUser, userResponse);

		userResponse = modelMapper.map(createdUser, UserResponse.class);

		return userResponse;
	}

	@PutMapping(path = "/update/{id}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public UserResponse updateUser(@PathVariable String id, @RequestBody UserDetailsRequest userDetailsRequest) {

		UserResponse response = new UserResponse();
		UserDTO userDTO = new UserDTO();

		BeanUtils.copyProperties(userDetailsRequest, userDTO);
		UserDTO upadtedUser = userService.updateUser(id, userDTO);

		BeanUtils.copyProperties(upadtedUser, response);
		return response;
	}

	@DeleteMapping(path = "/delete/{id}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public OperationStatusMOdel deleteUser(@PathVariable String id) {

		OperationStatusMOdel returnValue = new OperationStatusMOdel();
		returnValue.setName(RequestOperationName.DELETE.name());
		userService.deleteUser(id);
		returnValue.setResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public CollectionModel<AddressResponse> getUserAddresses(@PathVariable String id) {

		List<AddressResponse> returnValue = new ArrayList<AddressResponse>();
		List<AddressDTO> addressesDTO = addressService.getAddresses(id);

		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			Type listType = new TypeToken<List<AddressResponse>>() {
			}.getType();
			returnValue = new ModelMapper().map(addressesDTO, listType);
			for(AddressResponse addressResponse : returnValue) {
				
				Link selfLink = WebMvcLinkBuilder
						.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
								.getUserAddress(id, addressResponse.getAddressId()))
						//.slash(userId)
						//.slash("addresses")
						//.slash(addressId)
						.withSelfRel();
				addressResponse.add(selfLink);
			}
		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
		
		Link selfLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
				//.slash(userId)
				//.slash("addresses")
				//.slash(addressId)
				.withSelfRel();
		

		//return returnValue;
		return CollectionModel.of(returnValue,userLink,selfLink);

	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public EntityModel<AddressResponse> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDTO addressDTO = addressService.getAddress(addressId);
		AddressResponse returnValue = new ModelMapper().map(addressDTO, AddressResponse.class);

		// http://localhost:8080/users/<userId>/address/{addressId}
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
		
		Link userAddressesLink = WebMvcLinkBuilder.
				linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
				//.slash(userId)
				//.slash("addresses")
				.withRel("addresses");
		
		Link selfLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
				//.slash(userId)
				//.slash("addresses")
				.slash(addressId)
				.withSelfRel();

		/*
		 * returnValue.add(userLink); returnValue.add(userAddressesLink);
		 * returnValue.add(selfLink);
		 */
		
	return	EntityModel.of(returnValue, Arrays.asList(userLink,userAddressesLink,selfLink));

	}

}
