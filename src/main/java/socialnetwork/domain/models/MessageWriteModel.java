package socialnetwork.domain.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class MessageWriteModel{
    private Long idOfSender;
    private List<Long> idListOfReceivers;
    private String text;
    private LocalDateTime date;

    public MessageWriteModel(Long idOfSender,
                             String text,
                             LocalDateTime date) {
        this.idOfSender = idOfSender;
        this.text = text;
        this.date = date;
    }

    public void setIdListOfReceivers(List<Long> idListOfReceivers) {
        this.idListOfReceivers = idListOfReceivers;
    }

    public Long getIdOfSender() {
        return idOfSender;
    }

    public List<Long> getIdListOfReceivers() {
        return idListOfReceivers;
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
        if (!(o instanceof MessageWriteModel)) return false;
        MessageWriteModel that = (MessageWriteModel) o;
        return Objects.equals(idOfSender, that.idOfSender) &&
                Objects.equals(idListOfReceivers, that.idListOfReceivers) &&
                Objects.equals(text, that.text) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfSender, idListOfReceivers, text, date);
    }
}
