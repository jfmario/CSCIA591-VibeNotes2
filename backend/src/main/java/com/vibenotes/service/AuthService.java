package com.vibenotes.service;

import com.vibenotes.dto.AuthResponse;
import com.vibenotes.dto.LoginRequest;
import com.vibenotes.dto.RegisterRequest;
import com.vibenotes.model.User;
import com.vibenotes.repository.UserRepository;
import com.vibenotes.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	public AuthResponse register(RegisterRequest request) {
		// Check if username already exists
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new RuntimeException("Username already exists");
		}

		// Create new user
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		userRepository.save(user);

		// Generate token
		String token = jwtUtil.generateToken(user.getUsername());

		return new AuthResponse(token, user.getUsername(), "User registered successfully");
	}

	public AuthResponse login(LoginRequest request) {
		// Authenticate user
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsername(),
						request.getPassword()
				)
		);

		// Generate token
		String token = jwtUtil.generateToken(request.getUsername());

		return new AuthResponse(token, request.getUsername(), "Login successful");
	}

}

