package com.harian.share.location.closersharelocation.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.EmailAlreadyExistedException;

import java.io.IOException;

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
		}
		return new ResponseEntity<Response<Object>>(response, response.getStatusCode());
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(
			@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(service.authenticate(request));
	}

	@PostMapping("/refresh-token")
	public void refreshToken(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		service.refreshToken(request, response);
	}
}
