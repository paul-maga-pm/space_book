package socialnetwork.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message extends Entity<Long>{
    private Long conversationId;
    private Long senderId;
    String text;
    LocalDateTime date;

    public Message(Long id,
                   Long conversationId,
                   Long senderId,
                   String text,
                   LocalDateTime date) {
        super(id);
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.text = text;
        this.date = date;
    }



    public Long getConversationId() {
        return conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        if (!super.equals(o)) return false;
        Message message = (Message) o;
        return Objects.equals(conversationId, message.conversationId) &&
                Objects.equals(senderId, message.senderId) &&
                Objects.equals(text, message.text) &&
                Objects.equals(date, message.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), conversationId, senderId, text, date);
    }
}
