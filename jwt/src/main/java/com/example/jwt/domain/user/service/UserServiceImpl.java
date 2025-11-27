package com.example.jwt.domain.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt.common.exception.CustomException;
import com.example.jwt.common.exception.ErrorCode;
import com.example.jwt.common.util.jwt.JwtTokenProvider;
import com.example.jwt.domain.user.dto.request.SignUpRequest;
import com.example.jwt.domain.user.dto.response.ProfileResponse;
import com.example.jwt.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtTokenProvider jwtTokenProvider;
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	@Override
	public void signUp(SignUpRequest signUpRequest) {
		
		// 해당 이메일을 가진 유저가 있는지 확인.
		userRepository.findByEmail(signUpRequest.getEmail())
		.ifPresent(existingUser -> {
			// 다른 이메일을 사용하세요
			throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
		});
		// 해당 이메일을 가진 유저가 없다면, 비밀번호를 해시화.
		signUpRequest.setPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));
		// 그 유저를 저장.
		userRepository.save(signUpRequest);
	}
	
	@Override
	public ProfileResponse getProfile(String authorizationHeader) {
		
		if (authorizationHeader == null || authorizationHeader.isEmpty()) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}
		
		return userRepository.findByEmail(jwtTokenProvider.extractEmail(authorizationHeader, true))
				.map(user -> ProfileResponse.builder()
						.email(user.getEmail())
						.name(user.getName())
						.role(user.getRole().getName())
						.build()
						)
				.orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
	}

}
