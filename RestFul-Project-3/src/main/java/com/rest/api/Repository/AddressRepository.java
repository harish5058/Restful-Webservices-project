package com.rest.api.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rest.api.entity.AddressEntity;
import com.rest.api.entity.UserEntity;


@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

	 List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	  AddressEntity findByAddressId(String addressId);
}
