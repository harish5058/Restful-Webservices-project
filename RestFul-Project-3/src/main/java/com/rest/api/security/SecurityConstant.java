package com.rest.api.security;

import com.rest.api.SpringApplicationContext;

public class SecurityConstant {

	public static final long EXPIRATION_TIME=864000000;
	public static final String TOKEN_PREFIX = "Beare ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users/login";
	//public static final String TOKEN_SECRET = "jf9i4jgu83nfl0";
	
	public static String getTokenSecret() {
		AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
		return appProperties.getTokenSecret();
	}
}
