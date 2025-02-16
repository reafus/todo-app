package deeper.into.you.todo_app.views.notes;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import deeper.into.you.todo_app.notes.security.SecurityUtils;

@Route("login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    public LoginView() {
        Button loginButton = new Button("Login with Keycloak", e -> {
            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.getPage().setLocation("/oauth2/authorization/keycloak");
            }
        });
        add(loginButton);
    }



    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        boolean isLoggedIn = SecurityUtils.isUserLoggedIn();
        System.out.println("User logged in: " + isLoggedIn);
        if (isLoggedIn) {
            beforeEnterEvent.forwardTo(MainView.class);
        }
    }
}
