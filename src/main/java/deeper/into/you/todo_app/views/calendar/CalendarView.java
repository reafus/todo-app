package deeper.into.you.todo_app.views.calendar;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import deeper.into.you.todo_app.notes.entity.Note;
import deeper.into.you.todo_app.notes.services.NoteService;
import deeper.into.you.todo_app.views.MainLayout;

import java.util.List;


@Route(value = "calendar", layout = MainLayout.class)
@PageTitle("Календарь")
public class CalendarView extends VerticalLayout {

    private final NoteService noteService;
    private final FullCalendar calendar;

    public CalendarView(NoteService noteService) {
        this.noteService = noteService;
        this.calendar = new FullCalendar();

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        configureCalendar();
        loadEvents();
        FlexLayout layout = new FlexLayout(calendar);
        layout.setHeight("50%");
        layout.setWidth("90%");
        layout.getStyle()
                .set("margin", "0 auto");

        add(layout);
        expand(layout);

        UI.getCurrent().getPage().addBrowserWindowResizeListener(e -> {
            calendar.updateSize();
        });
    }

    private void configureCalendar() {
        calendar.setSizeFull();
        calendar.getStyle().set("min-height", "100%");
    }

    private void loadEvents() {
        List<Note> notesWithDates = noteService.findAllWithTodoDate();
        System.out.println("Загружаем события: " + notesWithDates.size());

        UI currentUI = UI.getCurrent();
        if (currentUI == null) {
            throw new IllegalStateException("UI.getCurrent() вернул null.");
        }

        currentUI.access(() -> {
            notesWithDates.forEach(note -> {
                System.out.println("Добавляем событие: " + note.getTitle());
                calendar.addEvent(
                        note.getTitle(),
                        note.getTodoDate(),
                        note.getGroup().getId().toString()
                );
            });
        });
    }
}
