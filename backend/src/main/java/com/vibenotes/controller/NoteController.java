package com.vibenotes.controller;

import com.vibenotes.dto.CreateNoteRequest;
import com.vibenotes.dto.NoteResponse;
import com.vibenotes.dto.UpdateNoteRequest;
import com.vibenotes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class NoteController {

	@Autowired
	private NoteService noteService;

	@PostMapping
	public ResponseEntity<NoteResponse> createNote(
			@Valid @RequestBody CreateNoteRequest request,
			Authentication authentication) {
		String username = authentication.getName();
		NoteResponse note = noteService.createNote(username, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(note);
	}

	@GetMapping
	public ResponseEntity<List<NoteResponse>> getUserNotes(Authentication authentication) {
		String username = authentication.getName();
		List<NoteResponse> notes = noteService.getUserNotes(username);
		return ResponseEntity.ok(notes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<NoteResponse> getNoteById(
			@PathVariable Long id,
			Authentication authentication) {
		String username = authentication.getName();
		NoteResponse note = noteService.getNoteById(username, id);
		return ResponseEntity.ok(note);
	}

	@PutMapping("/{id}")
	public ResponseEntity<NoteResponse> updateNote(
			@PathVariable Long id,
			@Valid @RequestBody UpdateNoteRequest request,
			Authentication authentication) {
		String username = authentication.getName();
		NoteResponse note = noteService.updateNote(username, id, request);
		return ResponseEntity.ok(note);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteNote(
			@PathVariable Long id,
			Authentication authentication) {
		String username = authentication.getName();
		noteService.deleteNote(username, id);
		return ResponseEntity.noContent().build();
	}

}

