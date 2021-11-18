package socialnetwork.domain.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageDto extends Entity<Long>{
    private String text;
    private LocalDateTime date;

    public MessageDto(Long messageId, String text, LocalDateTime date) {
        super(messageId);
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof  MessageDto messageDto)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(text, messageDto.getText()) &&
                Objects.equals(date, messageDto.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, date);
    }
}
