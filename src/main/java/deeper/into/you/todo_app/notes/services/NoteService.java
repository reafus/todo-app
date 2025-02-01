package deeper.into.you.todo_app.notes.services;

import deeper.into.you.todo_app.notes.entity.Note;
import deeper.into.you.todo_app.notes.entity.NotesGroup;
import deeper.into.you.todo_app.notes.repositories.NoteRepository;
import deeper.into.you.todo_app.notes.repositories.NotesGroupRepository;
import deeper.into.you.todo_app.notes.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;
    private final NotesGroupRepository notesGroupRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, NotesGroupRepository notesGroupRepository) {
        this.noteRepository = noteRepository;
        this.notesGroupRepository = notesGroupRepository;
    }

    public List<Note> findAll() {
        return noteRepository.findAll();
    }

    public Note findById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found"));
    }

    @Transactional
    public Note save(Note note) {
        return noteRepository.save(note);
    }

    @Transactional
    public void delete(Long id) {
        Note note = findById(id);
        noteRepository.delete(note);
    }

    @Transactional
    public Note updateCompletionStatus(Long id, boolean isCompleted) {
        Note note = findById(id);
        note.setCompleted(isCompleted);
        return noteRepository.save(note);
    }

    public List<Note> getRootNotesByGroup(Long groupId) {
        return noteRepository.findByGroupIdAndParentNoteIsNull(groupId);
    }

    public List<Note> getSubNotes(Long parentNoteId) {
        return noteRepository.findByParentNoteId(parentNoteId);
    }

    public NotesGroup getGroupById(Long groupId) {
        return notesGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
    }

}
