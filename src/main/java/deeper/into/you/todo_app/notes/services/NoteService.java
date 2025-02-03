package deeper.into.you.todo_app.notes.services;

import deeper.into.you.todo_app.notes.dto.NoteDTO;
import deeper.into.you.todo_app.notes.entity.Note;
import deeper.into.you.todo_app.notes.entity.NotesGroup;
import deeper.into.you.todo_app.notes.repositories.NoteRepository;
import deeper.into.you.todo_app.notes.repositories.NotesGroupRepository;
import deeper.into.you.todo_app.notes.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<Note> getActiveRootNotesByGroup(Long groupId) {
        return noteRepository.findByGroupIdAndParentNoteIsNullAndIsCompletedFalse(groupId);
    }

    public List<Note> getCompletedRootNotesByGroup(Long groupId) {
        return noteRepository.findCompletedRootNotesByGroup(groupId);
    }

    @Transactional
    public void updateNoteAndChildrenCompletionStatus(Long noteId, boolean isCompleted) {
        Note note = findById(noteId);
        note.setCompleted(isCompleted);
        updateChildrenCompletionStatus(note, isCompleted);
        noteRepository.save(note);
    }

    private void updateChildrenCompletionStatus(Note parent, boolean isCompleted) {
        for (Note child : parent.getSubNotes()) {
            child.setCompleted(isCompleted);
            noteRepository.save(child);
            updateChildrenCompletionStatus(child, isCompleted);
        }
    }
    public List<NoteDTO> getCompletedRootNotesByGroupAsDTO(Long groupId) {
        List<Note> completedNotes = noteRepository.findCompletedRootNotesByGroup(groupId);
        return completedNotes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NoteDTO convertToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setSubNotes(note.getSubNotes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        return dto;
    }
}
