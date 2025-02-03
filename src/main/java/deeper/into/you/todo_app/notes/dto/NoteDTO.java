package deeper.into.you.todo_app.notes.dto;

import java.util.List;

public class NoteDTO {
    private Long id;
    private String title;
    private List<NoteDTO> subNotes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<NoteDTO> getSubNotes() {
        return subNotes;
    }

    public void setSubNotes(List<NoteDTO> subNotes) {
        this.subNotes = subNotes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
