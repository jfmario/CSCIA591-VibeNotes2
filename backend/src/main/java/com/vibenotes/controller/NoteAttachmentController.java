package com.vibenotes.controller;

import com.vibenotes.dto.AttachmentResponse;
import com.vibenotes.model.Note;
import com.vibenotes.model.NoteAttachment;
import com.vibenotes.repository.NoteAttachmentRepository;
import com.vibenotes.repository.NoteRepository;
import com.vibenotes.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/notes/{noteId}/attachments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class NoteAttachmentController {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private NoteAttachmentRepository attachmentRepository;

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping
	public ResponseEntity<AttachmentResponse> uploadAttachment(
			@PathVariable Long noteId,
			@RequestParam("file") MultipartFile file,
			Authentication authentication) {
		
		String username = authentication.getName();
		
		// Verify note belongs to user
		Note note = noteRepository.findByIdAndUserUsername(noteId, username)
				.orElseThrow(() -> new RuntimeException("Note not found or access denied"));

		try {
			// Store file
			String filename = fileStorageService.storeAttachment(file);

			// Create attachment record
			NoteAttachment attachment = new NoteAttachment();
			attachment.setFilename(filename);
			attachment.setOriginalFilename(file.getOriginalFilename());
			attachment.setFileSize(file.getSize());
			attachment.setContentType(file.getContentType());
			attachment.setNote(note);

			NoteAttachment savedAttachment = attachmentRepository.save(attachment);

			AttachmentResponse response = new AttachmentResponse(
					savedAttachment.getId(),
					savedAttachment.getOriginalFilename(),
					savedAttachment.getFileSize(),
					savedAttachment.getContentType(),
					savedAttachment.getUploadedAt()
			);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			throw new RuntimeException("Failed to upload attachment: " + e.getMessage());
		}
	}

	@GetMapping("/{attachmentId}")
	public ResponseEntity<Resource> downloadAttachment(
			@PathVariable Long noteId,
			@PathVariable Long attachmentId,
			Authentication authentication) {
		
		String username = authentication.getName();
		
		// Verify note belongs to user
		noteRepository.findByIdAndUserUsername(noteId, username)
				.orElseThrow(() -> new RuntimeException("Note not found or access denied"));

		NoteAttachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new RuntimeException("Attachment not found"));

		// Verify attachment belongs to note
		if (!attachment.getNote().getId().equals(noteId)) {
			throw new RuntimeException("Attachment does not belong to this note");
		}

		Resource resource = fileStorageService.loadAttachment(attachment.getFilename());

		String contentType = attachment.getContentType();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getOriginalFilename() + "\"")
				.body(resource);
	}

	@DeleteMapping("/{attachmentId}")
	public ResponseEntity<Void> deleteAttachment(
			@PathVariable Long noteId,
			@PathVariable Long attachmentId,
			Authentication authentication) {
		
		String username = authentication.getName();
		
		// Verify note belongs to user
		noteRepository.findByIdAndUserUsername(noteId, username)
				.orElseThrow(() -> new RuntimeException("Note not found or access denied"));

		NoteAttachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new RuntimeException("Attachment not found"));

		// Verify attachment belongs to note
		if (!attachment.getNote().getId().equals(noteId)) {
			throw new RuntimeException("Attachment does not belong to this note");
		}

		// Delete file from storage
		fileStorageService.deleteAttachment(attachment.getFilename());

		// Delete database record
		attachmentRepository.delete(attachment);

		return ResponseEntity.noContent().build();
	}

}

