package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import deeper.into.you.todo_app.notes.dto.NoteDTO;
import deeper.into.you.todo_app.notes.services.NoteService;

import java.util.List;



public class CompletedNotesGrid extends TreeGrid<NoteDTO> {

    private final NoteService noteService;
    private final TreeDataProvider<NoteDTO> dataProvider;
    private Runnable refreshCallback;

    public CompletedNotesGrid(NoteService noteService, Long groupId) {
        this.noteService = noteService;
        this.dataProvider = new TreeDataProvider<>(new TreeData<>());
        configureGrid(groupId);
    }

    private void configureGrid(Long groupId) {
        addHierarchyColumn(NoteDTO::getTitle)
                .setHeader("Завершенные")
                .setWidth("300px")
                .setFlexGrow(1);


        addComponentColumn(note -> new Button("Восстановить", e -> {
            noteService.updateNoteAndChildrenCompletionStatus(note.getId(), false);
            refreshGrid(groupId);
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        })).setWidth("min-content");

        addComponentColumn(this::createDeleteButton).setHeader("Удалить").setWidth("100px");


        setHeight("300px");
        setMaxHeight("40vh");
        setWidthFull();

        setDataProvider(dataProvider);
    }


    private Component createDeleteButton(NoteDTO note) {
        Button deleteButton = new Button("Удалить", e -> {
            deleteNote(note);
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return deleteButton;
    }

    private void deleteNote(NoteDTO note) {
        try {
            noteService.delete(note.getId());
            TreeData<NoteDTO> treeData = dataProvider.getTreeData();
            treeData.removeItem(note);
            dataProvider.refreshAll();
        } catch (Exception ex) {
            Notification.show("Ошибка: " + ex.getMessage());
        }
    }

    public void refreshGrid(Long groupId) {
        TreeData<NoteDTO> treeData = dataProvider.getTreeData();
        treeData.clear();

        List<NoteDTO> completedNotes = noteService.getCompletedRootNotesByGroupAsDTO(groupId);

        completedNotes.forEach(note -> {
            treeData.addItem(null, note);
            note.getSubNotes().forEach(subNote -> treeData.addItem(note, subNote));
        });

        dataProvider.refreshAll();
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

}