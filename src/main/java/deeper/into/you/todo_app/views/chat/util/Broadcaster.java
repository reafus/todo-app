package deeper.into.you.todo_app.views.chat.util;

import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import deeper.into.you.todo_app.chat.entity.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Component
@UIScope
public class Broadcaster {
    private static final List<Consumer<ChatMessage>> listeners = Collections.synchronizedList(new ArrayList<>());

    public static synchronized Registration register(Consumer<ChatMessage> listener) {
        listeners.add(listener);
        System.out.println("New listener registered. Total: " + listeners.size());
        return () -> {
            synchronized (Broadcaster.class) {
                listeners.remove(listener);
                System.out.println("Listener removed. Total: " + listeners.size());
            }
        };
    }

    public static synchronized void broadcast(ChatMessage message) {
        System.out.println("Broadcasting message to " + listeners.size() + " listeners");
        listeners.forEach(listener -> {
            try {
                listener.accept(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}