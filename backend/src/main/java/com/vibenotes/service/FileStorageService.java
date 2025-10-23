package com.vibenotes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	public FileStorageService(@Value("${file.upload.dir}") String uploadDir) {
		this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (IOException ex) {
			throw new RuntimeException("Could not create the directory where uploaded files will be stored.", ex);
		}
	}

	public String storeFile(MultipartFile file) {
		// Validate file
		if (file.isEmpty()) {
			throw new RuntimeException("Failed to store empty file");
		}

		// Validate file type
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new RuntimeException("Only image files are allowed");
		}

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
			Path targetLocation = this.fileStorageLocation.resolve(newFilename);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return newFilename;
		} catch (IOException ex) {
			throw new RuntimeException("Could not store file " + newFilename + ". Please try again!", ex);
		}
	}

	public void deleteFile(String filename) {
		try {
			Path filePath = this.fileStorageLocation.resolve(filename).normalize();
			Files.deleteIfExists(filePath);
		} catch (IOException ex) {
			// Log but don't throw - file deletion is not critical
		}
	}

}

