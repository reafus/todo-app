package deeper.into.you.todo_app.chat.service;

import deeper.into.you.todo_app.chat.entity.ChatMessage;
import deeper.into.you.todo_app.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatMessageRepository repository;

    @Autowired
    public ChatService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public void save(ChatMessage message) {
        repository.save(message);
    }

    public List<ChatMessage> findTop50ByOrderByTimestampAsc() {
        return repository.findTop50ByOrderByTimestampAsc();
    }
}
