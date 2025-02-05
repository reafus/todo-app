package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import deeper.into.you.todo_app.notes.entity.Note;
import deeper.into.you.todo_app.notes.services.NoteService;
import deeper.into.you.todo_app.views.notes.util.QuillEditor;

import java.util.function.Consumer;


public class NoteDialog extends Dialog {

    private NoteDialog(Note note,
                       Long groupId,
                       NoteService service,
                       Consumer<Note> saveHandler) { // Используем Consumer<Note> вместо Runnable
        TextField titleField = new TextField("Заголовок");
        titleField.setWidth("80%");

        QuillEditor contentField = new QuillEditor();
        contentField.getComponent();

        Div editorContainer = new Div(contentField);
        editorContainer.setWidth("80%");
        editorContainer.setHeight("300px");
        editorContainer.getStyle().set("padding", "10px");
        editorContainer.getStyle().set("border", "1px solid #ccc");
        editorContainer.getStyle().set("border-radius", "5px");
        editorContainer.getStyle().set("overflow", "hidden");
        editorContainer.getStyle().set("box-sizing", "border-box");

        ComboBox<Note> parentCombo = new ComboBox<>("Указать родительскую заметку");
        parentCombo.setWidth("80%");
        DatePicker todoDateField = new DatePicker("Срок выполнения");
        todoDateField.setWidth("80%");


        titleField.setValue(note.getTitle() != null ? note.getTitle() : "");
        contentField.setValue(note.getContent() != null ? note.getContent() : "");
        todoDateField.setValue(note.getTodoDate());


        if (groupId != null) {
            parentCombo.setItems(service.getRootNotesByGroup(groupId));
            parentCombo.setItemLabelGenerator(Note::getTitle);
        }

        Button saveButton = new Button("Сохранить", e -> {

            note.setTitle(titleField.getValue());
            note.setTodoDate(todoDateField.getValue());


            contentField.getValue().thenAccept(content -> {
                note.setContent(content);


                if (parentCombo.getValue() != null) {
                    note.setParentNote(parentCombo.getValue());
                }


                if (groupId != null && note.getGroup() == null) {
                    note.setGroup(service.getGroupById(groupId));
                }


                try {
                    service.save(note);
                    saveHandler.accept(note);
                    close();
                } catch (Exception ex) {
                    Notification.show("Ошибка: " + ex.getMessage());
                }
            }).exceptionally(ex -> {
                Notification.show("Ошибка: " + ex.getMessage());
                return null;
            });
        });


        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.add(addCloseButton(), titleField, todoDateField, editorContainer, parentCombo, saveButton);

        setWidthFull();
        FlexLayout wrapper = new FlexLayout(layout);
        wrapper.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        wrapper.setSizeFull();

        add(wrapper);
    }


    public NoteDialog(Long groupId,
                      NoteService service,
                      Consumer<Note> saveHandler) {
        this(new Note(), groupId, service, saveHandler);
    }


    public NoteDialog(Note note,
                      NoteService service,
                      Consumer<Note> saveHandler) {
        this(note,
                note.getGroup() != null ? note.getGroup().getId() : null,
                service,
                saveHandler);
    }

    public HorizontalLayout addCloseButton() {
        Button closeButton = new Button(VaadinIcon.CLOSE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickListener(e -> close());
        closeButton.getStyle().set("margin-left", "auto");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        headerLayout.add(closeButton);
        return headerLayout;
    }
}
