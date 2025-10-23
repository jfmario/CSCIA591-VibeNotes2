package com.vibenotes.dto;

import java.time.LocalDateTime;

public class AttachmentResponse {

	private Long id;
	private String originalFilename;
	private Long fileSize;
	private String contentType;
	private LocalDateTime uploadedAt;

	public AttachmentResponse() {
	}

	public AttachmentResponse(Long id, String originalFilename, Long fileSize, String contentType, LocalDateTime uploadedAt) {
		this.id = id;
		this.originalFilename = originalFilename;
		this.fileSize = fileSize;
		this.contentType = contentType;
		this.uploadedAt = uploadedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

}

