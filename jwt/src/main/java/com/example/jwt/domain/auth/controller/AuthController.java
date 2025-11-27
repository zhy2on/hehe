package com.example.jwt.domain.auth.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt.common.documentation.AuthDocumentation;
import com.example.jwt.common.util.jwt.JwtProperties;
import com.example.jwt.domain.auth.dto.request.LoginRequest;
import com.example.jwt.domain.auth.dto.response.TokenResponse;
import com.example.jwt.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthDocumentation{
    private final JwtProperties jwtProperties;
    private final AuthService authService;
    @Value("${app.cookie.domain}")
    private String cookieDomain;
    
    @Value("${app.cookie.secure}")
    private Boolean cookieSecure;
    
    @PostMapping("/sessions")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest.getEmail());
        TokenResponse tokenResponse = authService.login(loginRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken());
        
        // 쿠키를 만들거임
        String cookie = ResponseCookie.from("refresh_token", tokenResponse.getRefreshToken())
                .domain(cookieDomain)
                .path("/")
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshTokenExpiry()))
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .build()
                .toString();
        
        headers.add(HttpHeaders.SET_COOKIE, cookie);
        
        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
    
    @DeleteMapping("/sessions")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @CookieValue("refresh_token") String refreshToken
    ) {
    	
    	authService.logout(authorizationHeader,refreshToken);
    	
        HttpHeaders headers = new HttpHeaders();
        
        // 쿠키를 만들거임
        String cookie = ResponseCookie.from("refresh_token", "")
                .domain(cookieDomain)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .build()
                .toString();
        
        headers.add(HttpHeaders.SET_COOKIE, cookie);
        
        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
    
    @GetMapping("/refresh-token")
    public ResponseEntity<?> createNewAccessToken(@CookieValue("refresh_token")String refreshToken){

        TokenResponse tokenResponse = authService.createNewAccessToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
    
}
