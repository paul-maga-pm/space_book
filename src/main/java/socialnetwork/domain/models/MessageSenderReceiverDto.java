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

    public boolean messageIsSentOrReceivedByUser(Long idOfUser) {
        return getId().getSenderId().equals(idOfUser) ||
                getId().getReceiverId().equals(idOfUser);
    }

    public boolean isMessageSentBy(Long idOfUser){
        return this.getId().getSenderId().equals(idOfUser);
    }

    public boolean isMessageReceivedBy(Long idOfUser){
        return this.getId().getReceiverId().equals(idOfUser);
    }

    public boolean isOfMessage(Long idOfMessage) {
        return this.getId().getMessageId().equals(idOfMessage);
    }
}
