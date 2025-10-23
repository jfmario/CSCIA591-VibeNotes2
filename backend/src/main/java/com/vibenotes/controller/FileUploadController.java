package com.vibenotes.controller;

import com.vibenotes.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class FileUploadController {

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/avatar")
	public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
		try {
			String filename = fileStorageService.storeFile(file);
			String fileUrl = "/uploads/avatars/" + filename;

			Map<String, String> response = new HashMap<>();
			response.put("url", fileUrl);
			response.put("message", "File uploaded successfully");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

}

