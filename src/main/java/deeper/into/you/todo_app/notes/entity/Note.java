package deeper.into.you.todo_app.notes.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "note")
public class Note extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_completed")
    private boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private NotesGroup group;

    @OneToMany(mappedBy = "parentNote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<Note> subNotes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_note_id")
    private Note parentNote;

    @Column(name = "todo_date")
    private LocalDate todoDate;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = Jsoup.clean(content, Safelist.basic()); //очистка от опасных тегов (в quill нет защиты от xss)
    }

    public NotesGroup getGroup() {
        return group;
    }

    public void setGroup(NotesGroup group) {
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Note getParentNote() {
        return parentNote;
    }

    public void setParentNote(Note parentNote) {
        this.parentNote = parentNote;
    }

    public List<Note> getSubNotes() {
        return subNotes;
    }

    public void setSubNotes(List<Note> subNotes) {
        this.subNotes = subNotes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getTodoDate() {
        return todoDate;
    }

    public void setTodoDate(LocalDate todoDate) {
        this.todoDate = todoDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
