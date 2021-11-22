package socialnetwork.domain.models;

import java.util.Objects;

public class MessageSenderReceiverDto extends Entity<MessageSenderReceiverDtoId>{

    public MessageSenderReceiverDto(Long messageId, Long senderId, Long receiverId) {
        super(new MessageSenderReceiverDtoId(messageId, senderId, receiverId));
    }

    public Long getMessageId(){
        return getId().getMessageId();
    }

    public Long getIdOfSender(){
        return getId().getSenderId();
    }

    public Long getIdOfReceiver(){
        return getId().getReceiverId();
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

    public boolean isSentBy(Long idOfUser){
        return this.getId().getSenderId().equals(idOfUser);
    }

    public boolean isReceivedBy(Long idOfUser){
        return this.getId().getReceiverId().equals(idOfUser);
    }

    public boolean isSendOrReceivedByUser(Long idOfUser){
        return isSentBy(idOfUser) || isReceivedBy(idOfUser);
    }

    public boolean isOfMessage(Long idOfMessage) {
        return this.getId().getMessageId().equals(idOfMessage);
    }
}
