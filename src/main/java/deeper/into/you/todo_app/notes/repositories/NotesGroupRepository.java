package deeper.into.you.todo_app.notes.repositories;

import deeper.into.you.todo_app.notes.entity.NotesGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotesGroupRepository extends JpaRepository<NotesGroup, Long> {
}
