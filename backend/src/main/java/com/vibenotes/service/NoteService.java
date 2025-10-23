package com.vibenotes.service;

import com.vibenotes.dto.CreateNoteRequest;
import com.vibenotes.dto.NoteResponse;
import com.vibenotes.dto.UpdateNoteRequest;
import com.vibenotes.model.Note;
import com.vibenotes.model.User;
import com.vibenotes.repository.NoteRepository;
import com.vibenotes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private UserRepository userRepository;

	public NoteResponse createNote(String username, CreateNoteRequest request) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		Note note = new Note();
		note.setTitle(request.getTitle());
		note.setContent(request.getContent());
		note.setUser(user);

		Note savedNote = noteRepository.save(note);
		return mapToNoteResponse(savedNote);
	}

	public List<NoteResponse> getUserNotes(String username) {
		return noteRepository.findByUserUsernameOrderByUpdatedAtDesc(username).stream()
				.map(this::mapToNoteResponse)
				.collect(Collectors.toList());
	}

	public NoteResponse getNoteById(String username, Long id) {
		Note note = noteRepository.findByIdAndUserUsername(id, username)
				.orElseThrow(() -> new RuntimeException("Note not found or access denied"));
		return mapToNoteResponse(note);
	}

	public NoteResponse updateNote(String username, Long id, UpdateNoteRequest request) {
		Note note = noteRepository.findByIdAndUserUsername(id, username)
				.orElseThrow(() -> new RuntimeException("Note not found or access denied"));

		if (request.getTitle() != null && !request.getTitle().isEmpty()) {
			note.setTitle(request.getTitle());
		}
		if (request.getContent() != null) {
			note.setContent(request.getContent());
		}

		Note updatedNote = noteRepository.save(note);
		return mapToNoteResponse(updatedNote);
	}

	public void deleteNote(String username, Long id) {
		Note note = noteRepository.findByIdAndUserUsername(id, username)
				.orElseThrow(() -> new RuntimeException("Note not found or access denied"));
		noteRepository.delete(note);
	}

	private NoteResponse mapToNoteResponse(Note note) {
		return new NoteResponse(
				note.getId(),
				note.getTitle(),
				note.getContent(),
				note.getUser().getUsername(),
				note.getCreatedAt(),
				note.getUpdatedAt()
		);
	}

}

