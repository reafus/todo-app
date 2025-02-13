package deeper.into.you.todo_app.notes.services;

import deeper.into.you.todo_app.notes.entity.NotesGroup;
import deeper.into.you.todo_app.notes.repositories.NotesGroupRepository;
import deeper.into.you.todo_app.notes.util.EntityNotFoundException;
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
        String userSub = SecurityUtils.getCurrentUserId();
        return notesGroupRepository.findAllByUserSub(userSub);
    }
    public Optional<NotesGroup> findById(Long id) {
        String userSub = SecurityUtils.getCurrentUserId();
        return notesGroupRepository.findByIdAndUserSub(id, userSub);
    }

    @Transactional
    public NotesGroup save(NotesGroup group) {
        String userSub = SecurityUtils.getCurrentUserId();
        group.setUserSub(userSub);
        return notesGroupRepository.save(group);
    }

    @Transactional
    public void delete(Long id) {
        String userSub = SecurityUtils.getCurrentUserId();
        NotesGroup group = notesGroupRepository.findByIdAndUserSub(id, userSub)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
        notesGroupRepository.delete(group);
    }

}
