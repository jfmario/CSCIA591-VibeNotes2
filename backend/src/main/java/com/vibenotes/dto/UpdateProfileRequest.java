package com.vibenotes.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

	@Size(max = 1000, message = "Description must not exceed 1000 characters")
	private String description;
	
	@Size(max = 500, message = "Avatar URL must not exceed 500 characters")
	private String avatarUrl;

	public UpdateProfileRequest() {
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

}

