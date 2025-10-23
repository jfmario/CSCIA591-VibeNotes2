package com.vibenotes.controller;

import com.vibenotes.dto.NoteResponse;
import com.vibenotes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/users/{username}/notes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class PublicNoteController {

	@Autowired
	private NoteService noteService;

	@GetMapping
	public ResponseEntity<List<NoteResponse>> getPublicNotes(@PathVariable String username) {
		List<NoteResponse> notes = noteService.getPublicNotesByUsername(username);
		return ResponseEntity.ok(notes);
	}

}

