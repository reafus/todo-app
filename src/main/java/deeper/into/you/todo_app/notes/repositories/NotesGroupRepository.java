package deeper.into.you.todo_app.notes.repositories;

import deeper.into.you.todo_app.notes.entity.NotesGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotesGroupRepository extends JpaRepository<NotesGroup, Long> {

    @Query("SELECT g FROM NotesGroup g WHERE g.userSub = :userSub")
    List<NotesGroup> findAllByUserSub(@Param("userSub") String userSub);

    @Query("SELECT g FROM NotesGroup g WHERE g.id = :groupId AND g.userSub = :userSub")
    Optional<NotesGroup> findByIdAndUserSub(@Param("groupId") Long groupId,
                                            @Param("userSub") String userSub);
}
