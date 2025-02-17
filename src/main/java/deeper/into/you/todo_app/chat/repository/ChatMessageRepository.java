package deeper.into.you.todo_app.chat.repository;

import deeper.into.you.todo_app.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage,String> {

    @Query(value = "{}", sort = "{timestamp: 1}")
    List<ChatMessage> findTop50ByOrderByTimestampAsc();

}
