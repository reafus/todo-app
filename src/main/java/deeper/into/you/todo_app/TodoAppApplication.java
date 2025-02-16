package deeper.into.you.todo_app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;

@SpringBootApplication
@Push(PushMode.AUTOMATIC)
@EnableJpaAuditing
@Theme("my-theme")
public class TodoAppApplication implements AppShellConfigurator {

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	public static void main(String[] args) {
		SpringApplication.run(TodoAppApplication.class, args);
	}

}
