package socialnetwork.domain.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReplyMessageWriteModel extends MessageWriteModel{
    private Long idOfMessageThatRepliesTo;

    public ReplyMessageWriteModel(Long idOfMessageThatRepliesTo,
                                  Long idOfSender,
                                  String text,
                                  LocalDateTime date) {
        super(idOfSender, text, date);
        this.idOfMessageThatRepliesTo = idOfMessageThatRepliesTo;
    }

    public Long getIdOfMessageThatRepliesTo() {
        return idOfMessageThatRepliesTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplyMessageWriteModel)) return false;
        if (!super.equals(o)) return false;
        ReplyMessageWriteModel that = (ReplyMessageWriteModel) o;
        return Objects.equals(idOfMessageThatRepliesTo, that.idOfMessageThatRepliesTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idOfMessageThatRepliesTo);
    }
}
