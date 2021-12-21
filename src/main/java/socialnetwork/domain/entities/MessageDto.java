package socialnetwork.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageDto {
    private User sender;
    private String text;
    private LocalDateTime date;

    public MessageDto(User sender,
                      String text,
                      LocalDateTime date) {
        this.sender = sender;
        this.text = text;
        this.date = date;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "sender=" + sender +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }

    public User getSender() {
        return sender;
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
        if (!(o instanceof MessageDto)) return false;
        MessageDto that = (MessageDto) o;
        return Objects.equals(sender, that.sender) &&
                Objects.equals(text, that.text) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, text, date);
    }
}
