package com.rest.api.Exception;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(value = {UserServiceException.class})
	public ResponseEntity<Object> handleUserServiceException( UserServiceException ex, WebRequest request){
		
		ErrorMessageResponse errorResponse = new ErrorMessageResponse(new Date(), ex.getMessage());
		
		return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> handleOtherException(Exception ex, WebRequest request){
		
		ErrorMessageResponse errorResponse = new ErrorMessageResponse(new Date(),ex.getMessage());
		
		return new ResponseEntity<>(errorResponse,new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
