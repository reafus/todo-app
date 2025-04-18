package deeper.into.you.todo_app.views.chat;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import deeper.into.you.todo_app.chat.entity.ChatMessage;
import deeper.into.you.todo_app.chat.service.ChatService;
import deeper.into.you.todo_app.notes.security.SecurityUtils;
import deeper.into.you.todo_app.views.MainLayout;
import deeper.into.you.todo_app.views.chat.util.Broadcaster;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.compress.utils.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Route(value = "chat", layout = MainLayout.class)
@PageTitle("Чат")
@PermitAll
public class ChatView extends VerticalLayout {

    private final GridFsTemplate gridFsTemplate;
    private final ChatService service;
    private final VerticalLayout messageLayout = new VerticalLayout();
    private final TextField messageField = new TextField();
    private final Upload upload = new Upload();
    private Registration broadcasterRegistration;

    public ChatView(GridFsTemplate gridFsTemplate, ChatService service) {
        this.gridFsTemplate = gridFsTemplate;
        this.service = service;
        configureUI();
        loadMessages();
        setupPushUpdates();
    }

    private void configureUI() {
        setSizeFull();
        messageField.setPlaceholder("Введите сообщение...");
        messageField.setWidthFull();
        messageField.addClassName("chat-view-text-field");

        Button sendButton = new Button("Отправить", VaadinIcon.ARROW_UP.create());
        sendButton.addClickShortcut(Key.ENTER);
        sendButton.addClickListener(event -> sendMessage());

        MemoryBuffer buffer = new MemoryBuffer();
        upload.setReceiver(buffer);
        upload.addSucceededListener(event -> handleFileUpload(buffer, event));

        HorizontalLayout inputLayout = new HorizontalLayout(messageField, sendButton, upload);
        inputLayout.setWidthFull();
        inputLayout.setSpacing(true);

        messageLayout.setSizeFull();
        messageLayout.getStyle().set("overflow", "auto");
        messageLayout.setPadding(false);
        messageLayout.addClassName("chat-messages");

        add(messageLayout, inputLayout);
        setFlexGrow(1, messageLayout);
    }

    private void sendMessage() {
        String text = messageField.getValue();
        if (text == null || text.trim().isEmpty()) return;

        ChatMessage message = new ChatMessage();
        message.setContent(text);
        message.setSender(SecurityUtils.getCurrentUsername());
        message.setTimestamp(LocalDateTime.now());

        service.save(message);
        messageField.clear();

        UI.getCurrent().access(() -> Broadcaster.broadcast(message));

    }

    private void handleFileUpload(MemoryBuffer buffer, SucceededEvent event) {
        try {
            byte[] bytes = IOUtils.toByteArray(buffer.getInputStream());

            String mediaUrl = saveMediaToMongo(event.getFileName(), bytes, event.getMIMEType());

            ChatMessage message = new ChatMessage();
            message.setContent("Прикреплён файл: " + event.getFileName());
            message.setSender(SecurityUtils.getCurrentUsername());
            message.setTimestamp(LocalDateTime.now());
            message.setMediaUrl(mediaUrl);
            message.setMediaType(event.getMIMEType());

            service.save(message);

            UI.getCurrent().access(() -> Broadcaster.broadcast(message));

        } catch (IOException e) {
            Notification.show("Ошибка загрузки файла");
        }
    }
    private String saveMediaToMongo(String filename, byte[] content, String mimeType) {
        ObjectId fileId = gridFsTemplate.store(
                new ByteArrayInputStream(content),
                filename,
                mimeType,
                new Document()
        );
        return fileId.toString();
    }

    private void loadMessages() {
        messageLayout.removeAll();
        service.findTop50ByOrderByTimestampAsc().forEach(this::displayMessage);
    }

    private void displayMessage(ChatMessage message) {
        try {
            Div messageDiv = new Div();
            messageDiv.addClassNames("chat-message");

            String currentUser = SecurityUtils.getCurrentUsername();
            boolean isCurrentUser = currentUser.equals(message.getSender());

            if(isCurrentUser) {
                messageDiv.addClassName("my-message");
            } else {
                messageDiv.addClassName("other-message");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String time = message.getTimestamp().format(formatter);

            HorizontalLayout header = new HorizontalLayout();
            header.addClassNames("message-header");
            header.setSpacing(false);
            header.setPadding(false);

            Span sender = new Span(message.getSender() + ": ");
            sender.addClassNames("sender", isCurrentUser ? "my-sender" : "other-sender");

            Span timeSpan = new Span(time);
            timeSpan.addClassNames("message-time");

            header.add(sender, timeSpan);
            header.setFlexGrow(1, timeSpan);
            header.setJustifyContentMode(JustifyContentMode.BETWEEN);

            Span content = new Span(message.getContent());
            content.addClassNames("message-content");

            VerticalLayout messageContent = new VerticalLayout(header, content);
            messageContent.setSpacing(false);
            messageContent.setPadding(false);

            if (message.getMediaUrl() != null) {
                String mediaUrl = "/api/media/" + message.getMediaUrl();
                if (message.getMediaType().startsWith("image/")) {
                    Image image = new Image(mediaUrl, "Прикреплённое изображение");
                    image.setMaxWidth("300px");
                    image.addClassName("chat-image");
                    messageDiv.add(image);
                } else {
                    Anchor download = new Anchor(mediaUrl, "Скачать файл");
                    download.setTarget("_blank");
                    messageDiv.add(download);
                }
            }
            messageDiv.add(messageContent);
            messageLayout.add(messageDiv);

        } catch (Exception e) {
            System.err.println("Ошибка при отображении сообщения:");
            e.printStackTrace();
        }
    }
    private void setupPushUpdates() {
        SecurityContext context = SecurityContextHolder.getContext();
        UI ui = UI.getCurrent();
        broadcasterRegistration = Broadcaster.register(newMessage -> {
            if (ui.isClosing() || !ui.isAttached()) return;

            ui.access(() -> {
                try {
                    SecurityContextHolder.setContext(context);
                    displayMessage(newMessage);
                    messageLayout.getElement().executeJs("setTimeout(() => {" +
                                "const msgDiv = document.querySelector('.chat-messages');" +
                                "msgDiv.scrollTop = msgDiv.scrollHeight;" +
                                "}, 100);");
                    } catch (Exception e) {
                    System.err.println("Ошибка при обновлении UI:");
                    e.printStackTrace();
                }
            });
        });

        addDetachListener(e -> broadcasterRegistration.remove());
    }


}
