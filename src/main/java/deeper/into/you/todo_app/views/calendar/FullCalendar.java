package deeper.into.you.todo_app.views.calendar;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;

import java.time.LocalDate;

@JsModule("./fullcalendar-wrapper.js")
@CssImport(value = "./styles/fullcalendar.css", themeFor = "vaadin-details")
public class FullCalendar extends Div {
    private Element calendarEl;

    public FullCalendar() {
        calendarEl = new Element("div");
        getElement().appendChild(calendarEl);
        getElement().executeJs("window.initCalendar($0)", calendarEl);
    }

    public void addEvent(String title, LocalDate date, String groupId) {
        System.out.println("adding event " + title);
        calendarEl.executeJs("this.addEvent($0, $1, $2)",
                title,
                date.toString(),
                groupId);
    }

    public void updateSize() {
        calendarEl.executeJs("this.calendar.updateSize()");
    }
}
