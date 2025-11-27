package com.example.jwt.domain.user.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	private String uuid;
	private String email;
	private String password;
	private String name;
	private Role role;
	
	
}
