package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import deeper.into.you.todo_app.notes.entity.NotesGroup;
import deeper.into.you.todo_app.notes.services.NotesGroupService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;


public class GroupDialog extends Dialog {
    private final Binder<NotesGroup> binder = new Binder<>(NotesGroup.class);
    private final TextField nameField = new TextField("Наименование группы");

    public GroupDialog(NotesGroupService service, Runnable refreshCallback) {
        this(new NotesGroup(), service, refreshCallback);
    }

    public GroupDialog(NotesGroup group, NotesGroupService service, Runnable refreshCallback) {
        configureBinder(group);
        buildUI(service, refreshCallback);
    }

    private void configureBinder(NotesGroup group) {
        binder.forField(nameField)
                .asRequired("⚠ Поле обязательно для заполнения")
                .withValidator(name -> name.length() <= 100,
                        "Максимальная длина 100 символов")
                .bind(NotesGroup::getName, NotesGroup::setName);

        binder.setBean(group);
    }

    private void buildUI(NotesGroupService service, Runnable refreshCallback) {
        nameField.setWidthFull();
        nameField.setRequiredIndicatorVisible(true);

        Button saveButton = createSaveButton(service, refreshCallback);
        HorizontalLayout buttons = new HorizontalLayout(saveButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout layout = new VerticalLayout(
                addCloseButton(),
                nameField,
                buttons
        );
        layout.setPadding(true);
        layout.setSpacing(true);
        add(layout);
        setWidth("400px");
    }

    private Button createSaveButton(NotesGroupService service, Runnable refreshCallback) {
        Button saveButton = new Button("Сохранить", e -> saveGroup(service, refreshCallback));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }

    private void saveGroup(NotesGroupService service, Runnable refreshCallback) {
        if (binder.validate().isOk()) {
            try {
                service.save(binder.getBean());
                refreshCallback.run();
                close();
            } catch (DataIntegrityViolationException ex) {
                showError("Группа с таким названием уже существует");
            } catch (ConstraintViolationException ex) {
                showValidationErrors(ex);
            } catch (Exception ex) {
                showError("Ошибка сохранения: " + ex.getMessage());
            }
        }
    }
    private void showValidationErrors(ConstraintViolationException ex) {
        ex.getConstraintViolations().forEach(v -> {
            String errorMsg = v.getMessage();
            String field = v.getPropertyPath().toString();

            if (field.equals("name")) {
                nameField.setInvalid(true);
                nameField.setErrorMessage(errorMsg);
            }
        });
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
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
