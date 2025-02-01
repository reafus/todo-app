package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import deeper.into.you.todo_app.notes.entity.NotesGroup;
import deeper.into.you.todo_app.notes.services.NotesGroupService;

public class GroupDialog extends Dialog {

    public GroupDialog(NotesGroupService service, Runnable refreshCallback) {
        this(new NotesGroup(), service, refreshCallback);
    }

    public GroupDialog(NotesGroup group, NotesGroupService service, Runnable refreshCallback) {
        TextField nameField = new TextField("Наименование группы");
        nameField.setValue(group.getName() != null ? group.getName() : "");

        Button saveButton = new Button("Сохранить", e -> {
            group.setName(nameField.getValue());
            try {
                service.save(group);
                refreshCallback.run();
                close();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });

        add(new VerticalLayout(nameField, saveButton));
        setWidth("400px");
    }
}
