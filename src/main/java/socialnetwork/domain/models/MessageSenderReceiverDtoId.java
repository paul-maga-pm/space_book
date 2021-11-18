package socialnetwork.domain.models;

import java.util.Objects;

public class MessageSenderReceiverDtoId{
    private Long messageId;
    private Long senderId;
    private Long receiverId;

    public MessageSenderReceiverDtoId(Long messageId, Long senderId, Long receiverId) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public MessageSenderReceiverDtoId(MessageSenderReceiverDtoId other){
        this.messageId = other.getMessageId();
        this.senderId = other.getSenderId();
        this.receiverId = other.getReceiverId();
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageSenderReceiverDtoId)) return false;
        MessageSenderReceiverDtoId id = (MessageSenderReceiverDtoId) o;
        return Objects.equals(messageId, id.getMessageId()) && Objects.equals(senderId, id.getSenderId()) &&
                Objects.equals(receiverId, id.getReceiverId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, senderId, receiverId);
    }
}
