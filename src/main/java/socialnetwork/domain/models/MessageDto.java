package socialnetwork.domain.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageDto extends Entity<Long>{
    private String message;
    private LocalDateTime date;

    public MessageDto(Long messageId, String message, LocalDateTime date) {
        super(messageId);
        this.message = message;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof  MessageDto messageDto)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(message, messageDto.getMessage()) &&
                Objects.equals(date, messageDto.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message, date);
    }
}
