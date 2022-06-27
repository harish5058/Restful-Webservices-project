package com.rest.api.service;

import java.util.List;

import com.rest.api.sharedClass.DTO.AddressDTO;

public interface AddressService {

	public List<AddressDTO> getAddresses(String userId);
	public AddressDTO getAddress(String addressId);
}
