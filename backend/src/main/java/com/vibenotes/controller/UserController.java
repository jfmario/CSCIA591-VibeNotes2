package com.vibenotes.controller;

import com.vibenotes.dto.UpdateProfileRequest;
import com.vibenotes.dto.UserProfileResponse;
import com.vibenotes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/profile")
	public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
		String username = authentication.getName();
		UserProfileResponse profile = userService.getCurrentUserProfile(username);
		return ResponseEntity.ok(profile);
	}

	@PutMapping("/profile")
	public ResponseEntity<UserProfileResponse> updateProfile(
			@Valid @RequestBody UpdateProfileRequest request,
			Authentication authentication) {
		String username = authentication.getName();
		UserProfileResponse profile = userService.updateProfile(username, request);
		return ResponseEntity.ok(profile);
	}

	@GetMapping
	public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
		List<UserProfileResponse> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
		UserProfileResponse user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

}

