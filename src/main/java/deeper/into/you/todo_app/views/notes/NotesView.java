package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.*;
import deeper.into.you.todo_app.notes.entity.Note;
import deeper.into.you.todo_app.notes.services.NoteService;
import deeper.into.you.todo_app.views.MainLayout;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "notes/:groupID", layout = MainLayout.class)
@PageTitle("Заметки")
public class NotesView extends VerticalLayout implements BeforeEnterObserver {

    private final NoteService noteService;
    private Long groupId;
    private TreeGrid<Note> grid = new TreeGrid<>();
    private CompletedNotesGrid completedGrid;
    private Map<Long, Boolean> detailStates = new HashMap<>();
    private Map<Long, Boolean> expandedStates = new HashMap<>();
    private TreeDataProvider<Note> dataProvider;

    public NotesView(NoteService noteService) {
        this.noteService = noteService;
        this.completedGrid = new CompletedNotesGrid(noteService, groupId);
        completedGrid.setRefreshCallback(this::refreshGrid);  // решение проблемы с обновлением гридов. После смены статуса

        Details details = new Details();
        details.add(this.completedGrid);
        details.setWidth("100%");
        details.getStyle().set("min-width", "100%");
        configureGrid();

        FlexLayout wrapper = new FlexLayout(createAddButton(), grid, details);
        wrapper.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        wrapper.setSizeFull();
        add(wrapper);
        refreshGrid();

    }

    private void configureGrid() {
        grid.addHierarchyColumn(Note::getTitle)
                .setHeader("Заголовок")
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setKey("title")
                .setClassNameGenerator(note -> "truncate-text")
                .setPartNameGenerator(note -> {
                    if (note.getParentNote() == null) {
                        return "parent-note";
                    } else {
                        return "sub-note";
                    }
                });

        grid.getColumnByKey("title")
                        .setTooltipGenerator(Note::getTitle);
        grid.addExpandListener(event -> {
            Note note = event.getItems().iterator().next();
            expandedStates.put(note.getId(), true);
        });

        grid.addCollapseListener(event -> {
            Note note = event.getItems().iterator().next();
            expandedStates.put(note.getId(), false);
        });

        grid.addComponentColumn(note -> {
                    Checkbox checkbox = new Checkbox();
                    checkbox.setValue(note.isCompleted());
                    checkbox.addValueChangeListener(e -> {
                        noteService.updateCompletionStatus(note.getId(), e.getValue());
                        refreshGrid();
                    });
                    return checkbox;
                }).setHeader("Статус")
                .setWidth("100px")
                .setFlexGrow(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        grid.addColumn(note -> note.getTodoDate() != null ? note.getTodoDate().format(formatter) : "")
                .setHeader("Срок выполнения")
                .setSortable(true)
                .setComparator((note1, note2) -> {
                    if (note1.getTodoDate() == null && note2.getTodoDate() == null) return 0;
                    if (note1.getTodoDate() == null) return -1;
                    if (note2.getTodoDate() == null) return 1;
                    return note1.getTodoDate().compareTo(note2.getTodoDate());
                })
                .setAutoWidth(true)
                .addClassName("date-column");

        grid.addComponentColumn(this::createNoteActions)
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.setMinHeight("600px");
        grid.setColumnReorderingAllowed(true);
        grid.setWidthFull();

        TreeData<Note> treeData = new TreeData<>();
        dataProvider = new TreeDataProvider<>(treeData);
        grid.setDataProvider(dataProvider);
    }

    private void loadData() {
        TreeData<Note> treeData = dataProvider.getTreeData();
        treeData.clear();

        List<Note> rootNotes = noteService.getActiveRootNotesByGroup(groupId);

        treeData.addItems(rootNotes, note -> noteService.getSubNotes(note.getId()));

        rootNotes.forEach(note -> restoreExpandedState(note));

        dataProvider.refreshAll();
    }

    private void restoreExpandedState(Note note) {
        Boolean isExpanded = expandedStates.get(note.getId());
        if (isExpanded != null && isExpanded) {
            grid.expand(note);
        }

        List<Note> subNotes = noteService.getSubNotes(note.getId());
        for (Note subNote : subNotes) {
            restoreExpandedState(subNote);
        }
    }

    private Component createAddButton() {
        return new Button("Добавить заметку", e -> {
            NoteDialog dialog = new NoteDialog(groupId, noteService, newNote -> {
                noteService.save(newNote);
                refreshGrid();
            });
            dialog.open();
        });
    }

    private HorizontalLayout createNoteActions(Note note) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> {
            NoteDialog dialog = new NoteDialog(note, noteService, updatedNote -> {
                noteService.save(updatedNote);
                refreshGrid();
            });
            dialog.open();
        });
        edit.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        edit.getElement().setAttribute("aria-label", "Редактировать");

        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> deleteNote(note));
        delete.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        delete.getElement().setAttribute("aria-label", "Удалить");

        Details details = new Details();

        String content = note.getContent();
        if (content == null || content.trim().isEmpty()) {
            content = "<div>Нет содержимого</div>";
        } else if (!content.trim().startsWith("<")) {
            content = "<div>" + content + "</div>";
        } else {
            content = "<div>" + content + "</div>";
        }

        Html descriptionContent = new Html(content);
        descriptionContent.getStyle().set("white-space", "normal");
        descriptionContent.getStyle().set("max-width", "100%");
        descriptionContent.getStyle().set("padding", "10px");

        Div container = new Div(descriptionContent);
        container.getStyle().set("max-height", "200px");
        container.getStyle().set("max-width", "100%");
        container.getStyle().set("overflow", "auto");

        details.addContent(container);

        Boolean isOpen = detailStates.get(note.getId());
        details.setOpened(isOpen != null && isOpen);

        details.addOpenedChangeListener(event -> {
            detailStates.put(note.getId(), event.isOpened());
        });

        details.setWidth("100%");
        details.getStyle().set("min-width", "300px");

        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        actionsLayout.setWidthFull();
        actionsLayout.setSpacing(true);

        actionsLayout.addAndExpand(details);
        actionsLayout.add(edit, delete);
        actionsLayout.setFlexGrow(1, details);
        actionsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        return actionsLayout;
    }

    private void deleteNote(Note note) {
        try {
            noteService.delete(note.getId());
            refreshGrid();
        } catch (Exception ex) {
            Notification.show("Error: " + ex.getMessage());
        }
    }

    private void refreshGrid() {
        loadData();
        completedGrid.refreshGrid(groupId);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        groupId = Long.parseLong(
                event.getRouteParameters().get("groupID").orElse("0")
        );
        refreshGrid();
    }

}