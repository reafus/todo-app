package deeper.into.you.todo_app.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import deeper.into.you.todo_app.views.calendar.CalendarView;
import deeper.into.you.todo_app.views.notes.MainView;



public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private Button themeToggle;
    private enum Theme { LIGHT, DARK, CUSTOM }
    private Theme currentTheme = Theme.LIGHT;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addNavbarContent();
        addDrawerContent();
        initTheme();
    }

    private void addNavbarContent() {
        var toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        toggle.setTooltipText("Menu toggle");

        toggle.addClickListener(e -> {
            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.getPage().executeJs(
                        "setTimeout(() => { window.dispatchEvent(new Event('resize')); }, 300)");
            }
        });
        viewTitle = new H2();
        viewTitle.addClassName("view-title");
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE, LumoUtility.Flex.GROW);

        themeToggle = new Button(VaadinIcon.ADJUST.create());
        themeToggle.addClickListener(e -> toggleTheme());
        themeToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        themeToggle.setTooltipText("Сменить тему");
        themeToggle.getElement().getStyle().set("transition", "all 0.3s ease");

        Button logoutButton = new Button("Выход", VaadinIcon.SIGN_OUT.create(), e -> {
            UI.getCurrent().getPage().setLocation("/logout");
        });
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.setTooltipText("Выйти из системы");


        var header = new HorizontalLayout(toggle, viewTitle, themeToggle, logoutButton);
        header.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                LumoUtility.Padding.End.MEDIUM, LumoUtility.Width.FULL);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(viewTitle);

        addToNavbar(true, header);
    }

    private void toggleTheme() {
        currentTheme = switch(currentTheme) {
            case LIGHT -> Theme.DARK;
            case DARK -> Theme.CUSTOM;
            case CUSTOM -> Theme.LIGHT;
        };
        applyTheme(currentTheme);

        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.getPage().executeJs(
                    "localStorage.setItem('vaadinTheme', $0)",
                    currentTheme.name().toLowerCase()
            );
        }
    }

    private void applyTheme(Theme theme) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.getPage().executeJs("document.documentElement.setAttribute('theme', $0)", theme.name().toLowerCase());

            switch (theme) {
                case DARK -> {
                    themeToggle.setIcon(VaadinIcon.MOON.create());
                    themeToggle.setTooltipText("Светлая тема");
                }
                case CUSTOM -> {
                    themeToggle.setIcon(VaadinIcon.PAINTBRUSH.create());
                    themeToggle.setTooltipText("Тёмно-серая тема");
                }
                default -> {
                    themeToggle.setIcon(VaadinIcon.SUN_O.create());
                    themeToggle.setTooltipText("Тёмная тема");
                }
            }
        }
    }

    private void addDrawerContent () {
            var appName = new Span("Заметки");
            appName.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                    LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD,
                    LumoUtility.Height.XLARGE, LumoUtility.Padding.Horizontal.MEDIUM);

            addToDrawer(appName, new Scroller(createSideNav()));
    }

    private SideNav createSideNav () {
        SideNav sideNav = new SideNav();

        sideNav.addItem(new SideNavItem("Главная", MainView.class,
                VaadinIcon.WORKPLACE.create()));
        sideNav.addItem(new SideNavItem("Календарь", CalendarView.class,
                VaadinIcon.CALENDAR.create()));

        return sideNav;
    }

    private String getCurrentPageTitle () {
        if (getContent() == null) {
            return "";
        } else if (getContent() instanceof HasDynamicTitle titleHolder) {
            return titleHolder.getPageTitle();
        } else {
            var title = getContent().getClass().getAnnotation(PageTitle.class);
            return title == null ? "" : title.value();
        }
    }

    private void initTheme () {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.getPage().executeJs(
                    "return localStorage.getItem('vaadinTheme')"
            ).then(value -> {
                String storedTheme = value.asString();
                if (storedTheme != null) {
                    currentTheme = Theme.valueOf(storedTheme.toUpperCase());
                    ui.getPage().executeJs(
                            "document.documentElement.setAttribute('theme', $0)",
                            storedTheme
                    );
                    applyTheme(currentTheme);
                }
            });
        }
    }

    @Override
    protected void afterNavigation () {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }
}
