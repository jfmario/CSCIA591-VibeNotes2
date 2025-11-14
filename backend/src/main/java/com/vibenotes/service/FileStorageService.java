package com.vibenotes.service;

import com.vibenotes.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

	private final Path avatarStorageLocation;
	private final Path attachmentStorageLocation;
	
	// Allowed image MIME types for avatars
	private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
		"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
	);
	
	// Allowed file MIME types for attachments (restrict dangerous types)
	private static final List<String> ALLOWED_ATTACHMENT_TYPES = Arrays.asList(
		"application/pdf",
		"application/msword",
		"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
		"text/plain",
		"text/csv",
		"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
	);

	public FileStorageService(
			@Value("${file.upload.avatar.dir}") String avatarUploadDir,
			@Value("${file.upload.attachment.dir}") String attachmentUploadDir) {
		this.avatarStorageLocation = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
		this.attachmentStorageLocation = Paths.get(attachmentUploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.avatarStorageLocation);
			Files.createDirectories(this.attachmentStorageLocation);
		} catch (IOException ex) {
			throw new FileStorageException("Could not create the directory where uploaded files will be stored.", ex);
		}
	}

	public String storeAvatar(MultipartFile file) {
		// Validate file
		if (file.isEmpty()) {
			throw new FileStorageException("File cannot be empty");
		}

		// Validate file type
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
			throw new FileStorageException("Only image files (JPEG, PNG, GIF, WebP) are allowed");
		}

		return storeFileInternal(file, avatarStorageLocation);
	}

	public String storeAttachment(MultipartFile file) {
		// Validate file
		if (file.isEmpty()) {
			throw new FileStorageException("File cannot be empty");
		}

		// Validate file type
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_ATTACHMENT_TYPES.contains(contentType.toLowerCase())) {
			throw new FileStorageException("File type not allowed. Allowed types: PDF, DOC, DOCX, TXT, CSV, and images");
		}

		return storeFileInternal(file, attachmentStorageLocation);
	}

	private String storeFileInternal(MultipartFile file, Path storageLocation) {
		// Generate unique filename
		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
		if (originalFilename.contains("..")) {
			throw new FileStorageException("Filename contains invalid path sequence");
		}
		
		String fileExtension = "";
		if (originalFilename.contains(".")) {
			fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		String newFilename = UUID.randomUUID().toString() + fileExtension;

		try {
			// Additional path traversal check
			Path targetLocation = storageLocation.resolve(newFilename).normalize();
			if (!targetLocation.startsWith(storageLocation)) {
				throw new FileStorageException("Invalid file path");
			}

			// Copy file to the target location
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return newFilename;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file. Please try again!", ex);
		}
	}

	public Resource loadAttachment(String filename) {
		try {
			// Validate filename to prevent path traversal
			if (filename == null || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
				throw new FileStorageException("Invalid filename");
			}
			
			Path filePath = attachmentStorageLocation.resolve(filename).normalize();
			
			// Additional path traversal check
			if (!filePath.startsWith(attachmentStorageLocation)) {
				throw new FileStorageException("Invalid file path");
			}
			
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists() && resource.isReadable()) {
				return resource;
			} else {
				throw new FileStorageException("File not found or not readable");
			}
		} catch (MalformedURLException ex) {
			throw new FileStorageException("Invalid file path", ex);
		}
	}

	public void deleteAvatar(String filename) {
		try {
			Path filePath = this.avatarStorageLocation.resolve(filename).normalize();
			Files.deleteIfExists(filePath);
		} catch (IOException ex) {
			// Log but don't throw - file deletion is not critical
		}
	}

	public void deleteAttachment(String filename) {
		try {
			Path filePath = this.attachmentStorageLocation.resolve(filename).normalize();
			Files.deleteIfExists(filePath);
		} catch (IOException ex) {
			// Log but don't throw - file deletion is not critical
		}
	}

}

