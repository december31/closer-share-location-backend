package com.harian.share.location.closersharelocation.auth;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harian.share.location.closersharelocation.auth.dto.AuthenticationRequest;
import com.harian.share.location.closersharelocation.auth.dto.RegisterRequest;
import com.harian.share.location.closersharelocation.auth.dto.RequestOtpRequest;
import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.EmailAlreadyExistedException;
import com.harian.share.location.closersharelocation.exception.OtpAuthenticationException;
import com.harian.share.location.closersharelocation.exception.TokenAuthenticationException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;

import io.micrometer.common.lang.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;

	@PostMapping("/register")
	public ResponseEntity<Response<Object>> register(
			@RequestBody RegisterRequest request) {

		Response<Object> response;
		try {
			response = Response.builder()
					.status(HttpStatus.OK)
					.message("register successful")
					.data(service.register(request))
					.build();
		} catch (EmailAlreadyExistedException e) {
			response = Response.builder()
					.status(HttpStatus.CONFLICT)
					.message(e.getMessage())
					.data(null)
					.build();
		} catch (OtpAuthenticationException e) {
			response = Response.builder()
					.status(HttpStatus.UNAUTHORIZED)
					.message(e.getMessage())
					.data(null)
					.build();
		}
		return new ResponseEntity<Response<Object>>(response, response.getStatusCode());
	}

	@PostMapping("/request-otp")
	public ResponseEntity<Response<Object>> requestOtp(@RequestBody RequestOtpRequest request) {
		Response<Object> response;
		try {
			service.requestOtp(request);
			response = Response.builder()
					.status(HttpStatus.OK)
					.message("OTP is being sent to you, please check the spam emails if you didn't receive it")
					.data(null)
					.build();
		} catch (UnsupportedEncodingException | MessagingException e) {
			response = Response.builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.data(null)
					.build();
		}
		return new ResponseEntity<Response<Object>>(response, response.getStatusCode());
	}

	@PostMapping("/authenticate")
	public ResponseEntity<Response<Object>> authenticate(
			@RequestBody AuthenticationRequest request) {

		Response<Object> response;
		try {
			response = Response.builder()
					.status(HttpStatus.OK)
					.message("authenticate successful")
					.data(service.authenticate(request))
					.build();
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			response = Response.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(e.getMessage())
					.data(null)
					.build();
		}
		return new ResponseEntity<Response<Object>>(response, response.getStatusCode());
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<Response<Object>> refreshToken(@RequestHeader("RefreshToken") @Nullable String refreshToken) {
		Response<Object> response;
		try {
			response = Response.builder()
					.status(HttpStatus.OK)
					.message("token refreshed successful")
					.data(service.refreshToken(refreshToken))
					.build();
		} catch (TokenAuthenticationException e) {
			response = Response.builder()
					.status(HttpStatus.UNAUTHORIZED)
					.message(e.getMessage())
					.data(null)
					.build();
		} catch (IOException e) {
			e.printStackTrace();
			response = Response.builder()
					.status(HttpStatus.BAD_REQUEST)
					.message("Something goes wrong with your refresh token")
					.data(null)
					.build();
		}
		return new ResponseEntity<Response<Object>>(response, response.getStatusCode());
	}
}
