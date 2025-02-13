package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.*;
import deeper.into.you.todo_app.notes.entity.NotesGroup;
import deeper.into.you.todo_app.notes.services.NotesGroupService;
import deeper.into.you.todo_app.notes.services.SecurityUtils;
import deeper.into.you.todo_app.views.MainLayout;

import java.util.Collections;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Группы Заметок")
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private final NotesGroupService groupService;
    private final Grid<NotesGroup> grid = new Grid<>(NotesGroup.class);

    public MainView(NotesGroupService groupService) {
        this.groupService = groupService;
        configureGrid();
        add(createAddButton(), grid);
        expand(grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.removeAllColumns();
        grid.addComponentColumn(group -> {
            Button nameButton = new Button(group.getName(), e ->
                    navigateToNotesView(group.getId())
            );
            nameButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            return nameButton;
        }).setHeader("Группы");
        setWidthFull();
        grid.addComponentColumn(this::createActions);
        grid.setHeight("300px");
    }
    private void navigateToNotesView(Long groupId) {
        RouteParameters parameters = new RouteParameters(
                Collections.singletonMap("groupID", groupId.toString())
        );
        UI.getCurrent().navigate(NotesView.class, parameters);
    }

    private Button createAddButton() {
        return new Button("Добавить группу", e -> new GroupDialog(groupService, this::refreshGrid).open());
    }

    private HorizontalLayout createActions(NotesGroup group) {

        Button edit = new Button(new Icon(VaadinIcon.EDIT));
        edit.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        edit.getElement().setAttribute("aria-label", "Редактировать");
        Tooltip.forComponent(edit).setText("Редактировать группу");
        edit.addClickListener(e -> new GroupDialog(group, groupService, this::refreshGrid).open());


        Button delete = new Button(new Icon(VaadinIcon.TRASH));
        delete.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        delete.getElement().setAttribute("aria-label", "Удалить");
        Tooltip.forComponent(delete).setText("Удалить группу");
        delete.addClickListener(e -> deleteGroup(group));

        HorizontalLayout actionsLayout = new HorizontalLayout(edit, delete);
        actionsLayout.setJustifyContentMode(JustifyContentMode.END);
        actionsLayout.setWidthFull();

        return actionsLayout;
    }

    private void deleteGroup(NotesGroup group) {
        try {
            groupService.delete(group.getId());
            refreshGrid();
        } catch (Exception ex) {
            Notification.show("Error: " + ex.getMessage());
        }
    }

    private void refreshGrid() {
        grid.setItems(groupService.findAll());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!SecurityUtils.isUserLoggedIn()) {
            beforeEnterEvent.rerouteTo(LoginView.class);
        }
    }
}
