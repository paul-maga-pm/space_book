package socialnetwork.domain.models;

import java.util.Objects;

public class ReplyDto extends Entity<Long>{
    private Long idOfMessageThatIsRepliedTo;

    public ReplyDto(Long idOfReply, Long idOfMessageThatIsRepliedTo) {
        super(idOfReply);
        this.idOfMessageThatIsRepliedTo = idOfMessageThatIsRepliedTo;
    }

    public Long getIdOfMessageThatIsRepliedTo() {
        return idOfMessageThatIsRepliedTo;
    }

    public void setIdOfMessageThatIsRepliedTo(Long idOfMessageThatIsRepliedTo) {
        this.idOfMessageThatIsRepliedTo = idOfMessageThatIsRepliedTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof  ReplyDto replyDto)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(idOfMessageThatIsRepliedTo, replyDto.getIdOfMessageThatIsRepliedTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idOfMessageThatIsRepliedTo);
    }
}
