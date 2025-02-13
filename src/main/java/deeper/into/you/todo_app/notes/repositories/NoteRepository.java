package deeper.into.you.todo_app.notes.repositories;

import deeper.into.you.todo_app.notes.entity.Note;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    @EntityGraph(attributePaths = {"group", "parentNote"})
    List<Note> findAllByUserSub(String userSub);

    @Query("SELECT n FROM Note n WHERE n.group.id = :groupId AND n.parentNote IS NULL AND n.userSub = :userSub")
    List<Note> findByGroupIdAndParentNoteIsNullAndUserSub(@Param("groupId") Long groupId,
                                                          @Param("userSub") String userSub);

    @Query("SELECT n FROM Note n WHERE n.parentNote.id = :parentNoteId AND n.userSub = :userSub")
    List<Note> findByParentNoteIdAndUserSub(@Param("parentNoteId") Long parentNoteId,
                                            @Param("userSub") String userSub);

    @EntityGraph(attributePaths = {"group"})
    @Query("SELECT n FROM Note n WHERE n.todoDate IS NOT NULL AND n.userSub = :userSub")
    List<Note> findByTodoDateIsNotNullAndUserSub(@Param("userSub") String userSub);

    @EntityGraph(attributePaths = {"subNotes"})
    @Query("SELECT n FROM Note n WHERE n.group.id = :groupId AND n.parentNote IS NULL AND n.isCompleted = false AND n.userSub = :userSub")
    List<Note> findByGroupIdAndParentNoteIsNullAndIsCompletedFalseAndUserSub(@Param("groupId") Long groupId, @Param("userSub") String userSub);

    @EntityGraph(attributePaths = {"subNotes"})
    @Query("SELECT n FROM Note n WHERE n.isCompleted = true AND n.group.id = :groupId AND n.parentNote IS NULL AND n.userSub = :userSub")
    List<Note> findCompletedRootNotesByGroupAndUserSub(@Param("groupId") Long groupId, @Param("userSub") String userSub);



}
