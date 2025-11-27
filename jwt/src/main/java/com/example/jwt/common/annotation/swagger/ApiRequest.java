package com.example.jwt.common.annotation.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestBody
public @interface ApiRequest {
	Content[] content() default {};
	boolean required() default false;
	String description() default "";
}
