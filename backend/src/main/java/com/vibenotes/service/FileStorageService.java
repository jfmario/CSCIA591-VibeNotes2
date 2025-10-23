package com.vibenotes.service;

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
import java.util.UUID;

@Service
public class FileStorageService {

	private final Path avatarStorageLocation;
	private final Path attachmentStorageLocation;

	public FileStorageService(
			@Value("${file.upload.avatar.dir}") String avatarUploadDir,
			@Value("${file.upload.attachment.dir}") String attachmentUploadDir) {
		this.avatarStorageLocation = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
		this.attachmentStorageLocation = Paths.get(attachmentUploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.avatarStorageLocation);
			Files.createDirectories(this.attachmentStorageLocation);
		} catch (IOException ex) {
			throw new RuntimeException("Could not create the directory where uploaded files will be stored.", ex);
		}
	}

	public String storeAvatar(MultipartFile file) {
		// Validate file
		if (file.isEmpty()) {
			throw new RuntimeException("Failed to store empty file");
		}

		// Validate file type
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new RuntimeException("Only image files are allowed");
		}

		return storeFileInternal(file, avatarStorageLocation);
	}

	public String storeAttachment(MultipartFile file) {
		// Validate file
		if (file.isEmpty()) {
			throw new RuntimeException("Failed to store empty file");
		}

		return storeFileInternal(file, attachmentStorageLocation);
	}

	private String storeFileInternal(MultipartFile file, Path storageLocation) {
		// Generate unique filename
		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
		String fileExtension = "";
		if (originalFilename.contains(".")) {
			fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		String newFilename = UUID.randomUUID().toString() + fileExtension;

		try {
			// Check for invalid characters
			if (newFilename.contains("..")) {
				throw new RuntimeException("Filename contains invalid path sequence " + newFilename);
			}

			// Copy file to the target location
			Path targetLocation = storageLocation.resolve(newFilename);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return newFilename;
		} catch (IOException ex) {
			throw new RuntimeException("Could not store file " + newFilename + ". Please try again!", ex);
		}
	}

	public Resource loadAttachment(String filename) {
		try {
			Path filePath = attachmentStorageLocation.resolve(filename).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new RuntimeException("File not found: " + filename);
			}
		} catch (MalformedURLException ex) {
			throw new RuntimeException("File not found: " + filename, ex);
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

