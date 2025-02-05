package deeper.into.you.todo_app.notes.repositories;

import deeper.into.you.todo_app.notes.entity.Note;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    @EntityGraph(attributePaths = {"group", "parentNote"})
    List<Note> findAll();

    List<Note> findByGroupIdAndParentNoteIsNull(Long groupId);

    List<Note> findByParentNoteId(Long parentNoteId);

    @EntityGraph(attributePaths = {"group"})
    List<Note> findByTodoDateIsNotNull();

    @EntityGraph(attributePaths = {"subNotes"})
    @Query("SELECT n FROM Note n WHERE n.group.id = :groupId AND n.parentNote IS NULL AND n.isCompleted = false")
    List<Note> findByGroupIdAndParentNoteIsNullAndIsCompletedFalse(@Param("groupId") Long groupId);

    @EntityGraph(attributePaths = {"subNotes"})
    @Query("SELECT n FROM Note n WHERE n.isCompleted = true AND n.group.id = :groupId AND n.parentNote IS NULL")
    List<Note> findCompletedRootNotesByGroup(@Param("groupId") Long groupId);



}
