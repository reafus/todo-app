package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import deeper.into.you.todo_app.notes.entity.Note;
import deeper.into.you.todo_app.notes.services.NoteService;

import java.util.List;

public class CompletedNotesGrid extends TreeGrid<Note> {

    private final NoteService noteService;
    private final TreeDataProvider<Note> dataProvider;

    public CompletedNotesGrid(NoteService noteService) {
        this.noteService = noteService;
        this.dataProvider = new TreeDataProvider<>(new TreeData<>());
        configureGrid();

    }

    private void configureGrid() {
        addHierarchyColumn(Note::getTitle)
                .setHeader("Завершенные")
                .setWidth("300px")
                .setFlexGrow(0);

        addComponentColumn(this::createRestoreButton).setHeader("Вернуть").setWidth("100px");
        addComponentColumn(this::createDeleteButton).setHeader("Удалить").setWidth("100px");

        setHeight("300px");
        setWidthFull();

        setDataProvider(dataProvider);
    }

    private Component createRestoreButton(Note note) {
        if (note.getParentNote() != null) {
            return new Span();
        }
        Button restoreButton = new Button("Вернуть", e -> {
            moveToActive(note);
        });
        restoreButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return restoreButton;
    }

    private Component createDeleteButton(Note note) {
        Button deleteButton = new Button("Удалить", e -> {
            deleteNote(note);
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return deleteButton;
    }

    private void moveToActive(Note note) {
        note.setCompleted(false);
        noteService.save(note);
        TreeData<Note> treeData = dataProvider.getTreeData();
        removeNoteAndChildrenFromTreeData(note, treeData);
        dataProvider.refreshAll();

        if (getUI().isPresent()) {
            getUI().get().access(() -> {
                NotesView notesView = (NotesView) getUI().get().getCurrentView();
                notesView.moveToActive(note);
            });
        }
    }
    private void removeNoteAndChildrenFromTreeData(Note note, TreeData<Note> treeData) {
        if (treeData.contains(note)) {
            treeData.removeItem(note);
        }
        List<Note> subNotes = noteService.getSubNotes(note.getId());
        for (Note subNote : subNotes) {
            removeNoteAndChildrenFromTreeData(subNote, treeData);
        }
    }
    private void deleteNote(Note note) {
        try {
            noteService.delete(note.getId());
            TreeData<Note> treeData = dataProvider.getTreeData();
            treeData.removeItem(note);
            dataProvider.refreshAll();
        } catch (Exception ex) {
            Notification.show("Ошибка: " + ex.getMessage());
        }
    }

    public void addCompletedNote(Note note) {
        TreeData<Note> treeData = dataProvider.getTreeData();
        if (!treeData.contains(note)) {
            treeData.addItem(null, note);
        }
        List<Note> subNotes = noteService.getSubNotes(note.getId());
        for (Note subNote : subNotes) {
            if (!treeData.contains(subNote)) {
                treeData.addItem(note, subNote);
            }
        }
        dataProvider.refreshAll();
    }

    public void clear() {
        TreeData<Note> treeData = dataProvider.getTreeData();
        treeData.clear();
        dataProvider.refreshAll();
    }
}