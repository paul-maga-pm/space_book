package socialnetwork.domain.models;

import java.util.Objects;

public class MessageSenderReceiverDto extends Entity<MessageSenderReceiverDtoId>{

    public MessageSenderReceiverDto(Long messageId, Long senderId, Long receiverId) {
        super(new MessageSenderReceiverDtoId(messageId, senderId, receiverId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageSenderReceiverDto)) return false;
        if (!super.equals(o)) return false;
        MessageSenderReceiverDto that = (MessageSenderReceiverDto) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
