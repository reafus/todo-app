package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
        nameField.setWidthFull();

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
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");

        mainLayout.add(
                addCloseButton(),
                nameField,
                saveButton
        );
        add(mainLayout);
        setWidth("600px");
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
