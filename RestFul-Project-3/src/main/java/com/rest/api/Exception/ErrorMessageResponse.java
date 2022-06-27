package com.rest.api.Exception;

import java.util.Date;

public class ErrorMessageResponse {

	private Date timeStamp;
	private String message;

	public ErrorMessageResponse(Date timeStamp, String message) {
		super();
		this.timeStamp = timeStamp;
		this.message = message;
	}

	public ErrorMessageResponse() {
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
