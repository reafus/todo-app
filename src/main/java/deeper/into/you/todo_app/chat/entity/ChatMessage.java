package deeper.into.you.todo_app.chat.entity;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
public class ChatMessage {
    @Id
    private String id;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private String mediaUrl;
    private String mediaType;

    public ChatMessage() {
    }

    public ChatMessage(String content, String id, String mediaType, String mediaUrl, String sender, LocalDateTime timestamp) {
        this.content = content;
        this.id = id;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timeStamp) {
        this.timestamp = timeStamp;
    }
}
