package com.example.jwt.domain.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt.common.annotation.role.RequireRole;
import com.example.jwt.common.annotation.role.UserRole;

@RestController
@RequestMapping("/admin")
@RequireRole({UserRole.ADMIN})
public class AdminController {
	
	@GetMapping("/test")
	public ResponseEntity<?> test() {
		return ResponseEntity.ok("관리자만 볼 수 있음 ㅇㅇ");
	}
	
}
