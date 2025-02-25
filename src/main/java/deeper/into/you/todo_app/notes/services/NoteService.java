package deeper.into.you.todo_app.notes.services;

import deeper.into.you.todo_app.notes.notifications.NoteEvent;
import deeper.into.you.todo_app.notes.security.SecurityUtils;
import deeper.into.you.todo_app.notes.dto.NoteDTO;
import deeper.into.you.todo_app.notes.entity.Note;
import deeper.into.you.todo_app.notes.entity.NotesGroup;
import deeper.into.you.todo_app.notes.repositories.NoteRepository;
import deeper.into.you.todo_app.notes.repositories.NotesGroupRepository;
import deeper.into.you.todo_app.notes.util.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;
    private final NotesGroupRepository notesGroupRepository;
    private final KafkaTemplate<String, NoteEvent> kafkaTemplate;
    private final String topicName;

    @Autowired
    public NoteService(NoteRepository noteRepository, NotesGroupRepository notesGroupRepository,
                       KafkaTemplate<String, NoteEvent> kafkaTemplate,
                       @Value("${spring.kafka.topic.name}") String topicName) {
        this.noteRepository = noteRepository;
        this.notesGroupRepository = notesGroupRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public List<Note> findAll() {
        String userSub = SecurityUtils.getCurrentUserId();
        return noteRepository.findAllByUserSub(userSub);
    }

    public Note findById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found"));
        String userSub = SecurityUtils.getCurrentUserId();
        if (!note.getUserSub().equals(userSub)) {
            throw new AccessDeniedException("You do not have permission to access this note.");
        }
        return note;
    }

    @Transactional
    public Note save(Note note) {
        String userSub = SecurityUtils.getCurrentUserId();
        note.setUserSub(userSub);
        boolean isNew = note.getId() == null;
        Note savedNote = noteRepository.save(note);

        if (note.getTodoDate() != null) {
            String eventType = isNew ? "CREATED" : "UPDATED";
            sendNoteEvent(savedNote, eventType);
        }
        return savedNote;
    }

    @Transactional
    public void delete(Long id) {
        Note note = findById(id);
        sendNoteEvent(note, "DELETED");
        noteRepository.delete(note);
    }

    @Transactional
    public Note updateCompletionStatus(Long id, boolean isCompleted) {
        Note note = findById(id);
        note.setCompleted(isCompleted);
        Note updatedNote = noteRepository.save(note);

        return updatedNote;
    }

    private void sendNoteEvent(Note note, String eventType) {
        NoteEvent event = new NoteEvent();
        event.setNoteId(note.getId());
        event.setUserSub(note.getUserSub());
        event.setNoteTitle(note.getTitle());
        event.setNoteContent(note.getContent());
        event.setTodoDate(note.getTodoDate());
        event.setEventType(eventType);
        event.setUserEmail(SecurityUtils.getCurrentUserEmail());
        kafkaTemplate.send(topicName, event);
    }

    public List<Note> getRootNotesByGroup(Long groupId) {
        String userSub = SecurityUtils.getCurrentUserId();
        return noteRepository.findByGroupIdAndParentNoteIsNullAndUserSub(groupId, userSub);
    }

    public List<Note> getSubNotes(Long parentNoteId) {
        String userSub = SecurityUtils.getCurrentUserId();
        return noteRepository.findByParentNoteIdAndUserSub(parentNoteId, userSub);
    }

    public NotesGroup getGroupById(Long groupId) {
        String userSub = SecurityUtils.getCurrentUserId();
        return notesGroupRepository.findByIdAndUserSub(groupId, userSub)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
    }

    public List<Note> getActiveRootNotesByGroup(Long groupId) {
        String userSub = SecurityUtils.getCurrentUserId();
        return noteRepository.findByGroupIdAndParentNoteIsNullAndIsCompletedFalseAndUserSub(groupId, userSub);
    }

    public List<Note> getCompletedRootNotesByGroup(Long groupId) {
        String userSub = SecurityUtils.getCurrentUserId();
        return noteRepository.findCompletedRootNotesByGroupAndUserSub(groupId, userSub);
    }

    @Transactional
    public void updateNoteAndChildrenCompletionStatus(Long noteId, boolean isCompleted) {
        Note note = findById(noteId);
        note.setCompleted(isCompleted);
        updateChildrenCompletionStatus(note, isCompleted);
        noteRepository.save(note);
    }

    private void updateChildrenCompletionStatus(Note parent, boolean isCompleted) {
        String userSub = SecurityUtils.getCurrentUserId();
        for (Note child : parent.getSubNotes()) {
            if (!child.getUserSub().equals(userSub)) {
                continue; // Пропускаем чужие заметки
            }
            child.setCompleted(isCompleted);
            noteRepository.save(child);
            updateChildrenCompletionStatus(child, isCompleted);
        }
    }
    public List<NoteDTO> getCompletedRootNotesByGroupAsDTO(Long groupId) {
        String userSub = SecurityUtils.getCurrentUserId();
        List<Note> completedNotes = noteRepository.findCompletedRootNotesByGroupAndUserSub(groupId, userSub);
        return completedNotes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Note> findAllWithTodoDate() {
        String userSub = SecurityUtils.getCurrentUserId();
        return noteRepository.findByTodoDateIsNotNullAndUserSub(userSub);
    }

    private NoteDTO convertToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setSubNotes(note.getSubNotes().stream()
                .filter(subNote -> subNote.getUserSub().equals(SecurityUtils.getCurrentUserId())) // Фильтруем подзаметки по userSub
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        return dto;
    }
}
