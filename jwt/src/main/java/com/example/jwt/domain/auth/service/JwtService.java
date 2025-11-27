package com.example.jwt.domain.auth.service;

public interface JwtService {

	boolean isBlackListed(String token);
	
}
