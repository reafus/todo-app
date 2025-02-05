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
    //todo добавить темно серую третью тему

    private H2 viewTitle;
    private Button themeToggle;
    private boolean isDarkTheme = false;

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
            UI.getCurrent().getPage().executeJs(
                    "setTimeout(() => { window.dispatchEvent(new Event('resize')); }, 300)");
        });
        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE, LumoUtility.Flex.GROW);

        themeToggle = new Button(VaadinIcon.ADJUST.create());
        themeToggle.addClickListener(e -> toggleTheme());
        themeToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        themeToggle.setTooltipText("Сменить тему");
        themeToggle.getElement().getStyle().set("transition", "all 0.3s ease");


        var header = new HorizontalLayout(toggle, viewTitle, themeToggle);
        header.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                LumoUtility.Padding.End.MEDIUM, LumoUtility.Width.FULL);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(viewTitle);

        addToNavbar(true, header);
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        applyDarkTheme(isDarkTheme);


        UI.getCurrent().getPage().executeJs(
                "localStorage.setItem('vaadinTheme', $0)",
                isDarkTheme ? "dark" : "light"
        );
    }

    private void applyDarkTheme(boolean dark) {
        if(dark) {
            UI.getCurrent().getElement().setAttribute("theme", "dark");
            themeToggle.setIcon(VaadinIcon.MOON.create());
            themeToggle.setTooltipText("Светлая тема");
        } else {
            UI.getCurrent().getElement().setAttribute("theme", "light");
            themeToggle.setIcon(VaadinIcon.SUN_O.create());
            themeToggle.setTooltipText("Темная тема");
        }
    }

    private void addDrawerContent() {
        var appName = new Span("Заметки");
        appName.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.Height.XLARGE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToDrawer(appName, new Scroller(createSideNav()));
    }
    private SideNav createSideNav() {
        SideNav sideNav = new SideNav();

        sideNav.addItem(new SideNavItem("Главная", MainView.class,
                VaadinIcon.WORKPLACE.create()));
        sideNav.addItem(new SideNavItem("Календарь", CalendarView.class,
                VaadinIcon.CALENDAR.create()));

        return sideNav;
    }

    private String getCurrentPageTitle() {
        if (getContent() == null) {
            return "";
        } else if (getContent() instanceof HasDynamicTitle titleHolder) {
            return titleHolder.getPageTitle();
        } else {
            var title = getContent().getClass().getAnnotation(PageTitle.class);
            return title == null ? "" : title.value();
        }
    }

    private void initTheme() {
        UI.getCurrent().getPage().executeJs(
                "return localStorage.getItem('vaadinTheme')"
        ).then(value -> {
            if("dark".equals(value.asString())) {
                applyDarkTheme(true);
            }
        });
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }
}
