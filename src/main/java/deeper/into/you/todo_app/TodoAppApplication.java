package deeper.into.you.todo_app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Theme("my-theme")
public class TodoAppApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(TodoAppApplication.class, args);
	}

}
