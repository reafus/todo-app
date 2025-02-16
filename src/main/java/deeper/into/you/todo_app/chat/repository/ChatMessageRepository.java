package deeper.into.you.todo_app.chat.repository;

import deeper.into.you.todo_app.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage,String> {
    List<ChatMessage> findTop50ByOrderByTimestampDesc();
}
