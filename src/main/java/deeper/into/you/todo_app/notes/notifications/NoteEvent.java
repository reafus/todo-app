package deeper.into.you.todo_app.notes.notifications;

import java.time.LocalDate;


public class NoteEvent {
    private Long noteId;
    private String userSub;
    private String noteTitle;
    private String noteContent;
    private LocalDate todoDate;
    private String eventType;
    private String userEmail;

    public NoteEvent() {
    }

    public NoteEvent(String eventType, Long noteId, String noteTitle, String noteContent, LocalDate todoDate, String userSub, String userEmail) {
        this.eventType = eventType;
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.todoDate = todoDate;
        this.userSub = userSub;
        this.userEmail = userEmail;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public LocalDate getTodoDate() {
        return todoDate;
    }

    public void setTodoDate(LocalDate todoDate) {
        this.todoDate = todoDate;
    }

    public String getUserSub() {
        return userSub;
    }

    public void setUserSub(String userSub) {
        this.userSub = userSub;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}
