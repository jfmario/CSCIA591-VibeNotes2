package com.vibenotes.dto;

import jakarta.validation.constraints.Size;

public class UpdateNoteRequest {

	@Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
	private String title;

	private String content;

	public UpdateNoteRequest() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

