package com.example.jwt.domain.user.service;

import com.example.jwt.domain.user.dto.request.SignUpRequest;
import com.example.jwt.domain.user.dto.response.ProfileResponse;

public interface UserService {

	void signUp(SignUpRequest signUpRequest);
	
	public ProfileResponse getProfile(String authorizationHeader);

}
