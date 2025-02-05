import { Calendar } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import ruLocale from '@fullcalendar/core/locales/ru';

window.initCalendar = (element) => {
    const calendarEl = document.createElement('div');
    calendarEl.style.height = '100%';
    element.appendChild(calendarEl);

    const calendar = new Calendar(calendarEl, {
        plugins: [dayGridPlugin],
        initialView: 'dayGridMonth',
        eventClick: function (info) {
            const groupId = info.event.extendedProps.groupId;
            window.open(`notes/${groupId}`, '_self');
        },
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,dayGridWeek,dayGridDay'
        },
        locale: 'ru',
        locales: [ruLocale],
    });

    calendar.render();
    element.calendar = calendar;

    element.addEvent = (title, date, groupId) => {
        calendar.addEvent({
            title: title,
            start: date,
            allDay: true,
            extendedProps: { groupId: groupId }
        });
    };

    new ResizeObserver(() => calendar.updateSize()).observe(element);
};