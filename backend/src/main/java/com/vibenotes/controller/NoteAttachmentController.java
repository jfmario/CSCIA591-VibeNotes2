package com.vibenotes.controller;

import com.vibenotes.dto.AttachmentResponse;
import com.vibenotes.exception.ResourceNotFoundException;
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
@CrossOrigin
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
				.orElseThrow(() -> new ResourceNotFoundException("Note not found"));

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
	}

	@GetMapping("/{attachmentId}")
	public ResponseEntity<Resource> downloadAttachment(
			@PathVariable Long noteId,
			@PathVariable Long attachmentId,
			Authentication authentication) {
		
		String username = authentication.getName();
		
		// Verify note belongs to user
		noteRepository.findByIdAndUserUsername(noteId, username)
				.orElseThrow(() -> new ResourceNotFoundException("Note not found"));

		NoteAttachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

		// Verify attachment belongs to note
		if (!attachment.getNote().getId().equals(noteId)) {
			throw new ResourceNotFoundException("Attachment not found");
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
				.orElseThrow(() -> new ResourceNotFoundException("Note not found"));

		NoteAttachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

		// Verify attachment belongs to note
		if (!attachment.getNote().getId().equals(noteId)) {
			throw new ResourceNotFoundException("Attachment not found");
		}

		// Delete file from storage
		fileStorageService.deleteAttachment(attachment.getFilename());

		// Delete database record
		attachmentRepository.delete(attachment);

		return ResponseEntity.noContent().build();
	}

}

