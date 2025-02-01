package deeper.into.you.todo_app.notes.repositories;


import deeper.into.you.todo_app.notes.entity.Note;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    @EntityGraph(attributePaths = {"group", "parentNote"})
    List<Note> findAll();

    List<Note> findByGroupIdAndParentNoteIsNull(Long groupId);

    List<Note> findByParentNoteId(Long parentNoteId);
}
