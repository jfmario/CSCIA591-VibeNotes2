package com.vibenotes.service;

import com.vibenotes.dto.UpdateProfileRequest;
import com.vibenotes.dto.UserProfileResponse;
import com.vibenotes.model.User;
import com.vibenotes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public UserProfileResponse getCurrentUserProfile(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return mapToProfileResponse(user);
	}

	public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		if (request.getDescription() != null) {
			user.setDescription(request.getDescription());
		}
		if (request.getAvatarUrl() != null) {
			user.setAvatarUrl(request.getAvatarUrl());
		}

		User updatedUser = userRepository.save(user);
		return mapToProfileResponse(updatedUser);
	}

	public List<UserProfileResponse> getAllUsers() {
		return userRepository.findAll().stream()
				.map(this::mapToProfileResponse)
				.collect(Collectors.toList());
	}

	public UserProfileResponse getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
		return mapToProfileResponse(user);
	}

	private UserProfileResponse mapToProfileResponse(User user) {
		return new UserProfileResponse(
				user.getId(),
				user.getUsername(),
				user.getDescription(),
				user.getAvatarUrl(),
				user.getCreatedAt()
		);
	}

}

