package com.rest.api.Repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.rest.api.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>{

	public UserEntity findByEmail(String email);
	public UserEntity findUserByUserId(String userId);
}
