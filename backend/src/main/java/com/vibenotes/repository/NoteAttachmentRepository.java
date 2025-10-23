package com.vibenotes.repository;

import com.vibenotes.model.NoteAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteAttachmentRepository extends JpaRepository<NoteAttachment, Long> {

	List<NoteAttachment> findByNoteId(Long noteId);

	void deleteByNoteId(Long noteId);

}

