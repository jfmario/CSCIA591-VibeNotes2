package com.vibenotes.dto;

import java.time.LocalDateTime;

public class UserProfileResponse {

	private Long id;
	private String username;
	private String description;
	private String avatarUrl;
	private LocalDateTime createdAt;

	public UserProfileResponse() {
	}

	public UserProfileResponse(Long id, String username, String description, String avatarUrl, LocalDateTime createdAt) {
		this.id = id;
		this.username = username;
		this.description = description;
		this.avatarUrl = avatarUrl;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}

