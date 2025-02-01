package deeper.into.you.todo_app.notes.services;

import deeper.into.you.todo_app.notes.entity.NotesGroup;
import deeper.into.you.todo_app.notes.repositories.NotesGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class NotesGroupService {

    private final NotesGroupRepository notesGroupRepository;

    @Autowired
    public NotesGroupService(NotesGroupRepository notesGroupRepository) {
        this.notesGroupRepository = notesGroupRepository;
    }

    public List<NotesGroup> findAll() {
        return notesGroupRepository.findAll();
    }
    public Optional<NotesGroup> findById(Long id) {
        return notesGroupRepository.findById(id);
    }

    @Transactional
    public NotesGroup save(NotesGroup group) {
        return notesGroupRepository.save(group);
    }

    @Transactional
    public void delete(Long id) {
        notesGroupRepository.deleteById(id);
    }

}
