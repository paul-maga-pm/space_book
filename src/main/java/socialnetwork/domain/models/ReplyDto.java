package socialnetwork.domain.models;

import java.util.Objects;

public class ReplyDto extends Entity<Long>{
    private Long messageId;

    public ReplyDto(Long replyid, Long messageId) {
        super(replyid);
        this.messageId = messageId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof  ReplyDto replyDto)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(messageId, replyDto.getMessageId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), messageId);
    }
}
