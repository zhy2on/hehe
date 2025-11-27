package com.example.jwt.domain.auth.service;

import com.example.jwt.domain.auth.dto.request.LoginRequest;
import com.example.jwt.domain.auth.dto.response.TokenResponse;

public interface AuthService {

	TokenResponse login(LoginRequest loginRequest);

	void logout(String authorizationHeader, String refreshToken);
	
	TokenResponse createNewAccessToken(String refreshToken);

}
