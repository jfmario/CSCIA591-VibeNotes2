package com.vibenotes.controller;

import com.vibenotes.dto.AuthResponse;
import com.vibenotes.dto.ErrorResponse;
import com.vibenotes.dto.LoginRequest;
import com.vibenotes.dto.RegisterRequest;
import com.vibenotes.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
		try {
			AuthResponse response = authService.register(request);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(e.getMessage(), "Registration failed"));
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		try {
			AuthResponse response = authService.login(request);
			return ResponseEntity.ok(response);
		} catch (BadCredentialsException e) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(new ErrorResponse("Invalid username or password", "Authentication failed"));
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("An error occurred", e.getMessage()));
		}
	}

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("Auth endpoint is working!");
	}

}

