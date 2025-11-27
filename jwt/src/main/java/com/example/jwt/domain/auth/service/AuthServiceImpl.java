package com.example.jwt.domain.auth.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt.common.exception.CustomException;
import com.example.jwt.common.exception.ErrorCode;
import com.example.jwt.common.util.jwt.JwtProperties;
import com.example.jwt.common.util.jwt.JwtTokenProvider;
import com.example.jwt.domain.auth.dto.request.LoginRequest;
import com.example.jwt.domain.auth.dto.response.TokenResponse;
import com.example.jwt.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JwtService jwtService;
    private final JwtProperties jwtProperties;

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	private final RedisTemplate<String, Object> redisTemplate;
	
	
	@Override
	public TokenResponse login(LoginRequest loginRequest) {
		
		// BCrypt 경우 이건 안됨.
		// 다른 해시의 경우에는 이렇게 해도 무방.
		// loginRequest.setPassword(bCryptPasswordEncoder.encode(loginRequest.getPassword()));
		
		// 이메일 기반으로 User 를 조회
		// 그 User 가 가지고 있는 비밀번호와 로그인 요청의 비밀번호를 bCryptPasswordEncoder.matches 로 비교
		
		return userRepository.findByEmailAndPassword(loginRequest)
				.map(user -> TokenResponse.builder()
						.accessToken(jwtTokenProvider.createAccessToken(user))
						.refreshToken(jwtTokenProvider.createRefreshToken(user))
						.accessTokenExpiresIn(jwtProperties.getAccessTokenExpiry())
						.refreshTokenExpiresIn(jwtProperties.getRefreshTokenExpiry())
						.build())
				.orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED)); // Enum 쓰면 클래스 생성 개수를 줄일 수 있다!
	}
	
	@Override
	public void logout(String authorizationHeader, String refreshToken) {
		
		// authorizationHeader, refreshToken 이 빈 값으로 오는 경우도 존재.
		// 예외 처리는 필수
		String accessToken = authorizationHeader.substring(7);
		
		redisTemplate.opsForValue().set(accessToken, "BlackList");
		redisTemplate.opsForValue().set(refreshToken, "BlackList");
	
		// 정확하게 하려면, 토큰에서 만료시간을 추출하고,
		// 현재 시간과 만료 시간의 차이를 구해서,
		// 그 값을 redis 만료 시간으로 사용 -> JWT 와 Redis 에 등록된 만료시간이 맞춰지게 된다.
		redisTemplate.expire(accessToken, jwtProperties.getAccessTokenExpiry(), TimeUnit.MILLISECONDS);
		redisTemplate.expire(refreshToken, jwtProperties.getRefreshTokenExpiry(), TimeUnit.MILLISECONDS);
	}
	
	@Override
	public TokenResponse createNewAccessToken(String refreshToken) {
		return Optional.ofNullable(refreshToken)
				.filter(token -> !token.trim().isEmpty())
				.filter(token -> !jwtService.isBlackListed(token)) // jwtService -> 블랙리스트에 등록이 된 토큰인지 확인
				.map(token -> jwtTokenProvider.extractEmail(refreshToken, false))
				.flatMap(userRepository::findByEmail)
				.map(user -> TokenResponse.builder()
						.accessToken(jwtTokenProvider.createAccessToken(user))
						.accessTokenExpiresIn(jwtProperties.getAccessTokenExpiry())
						.build())
				.orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
	}
}
