package com.rest.api.Exception;

public class UserServiceException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UserServiceException(String messgae) {
		super(messgae);
	}
}
