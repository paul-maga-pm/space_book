package socialnetwork.utils.containers;

import java.util.Objects;

public class MessageSenderReceiverDtoId<T1, T2, T3>{
    private T1 messageId;
    private T2 senderId;
    private T3 receiverId;

    public MessageSenderReceiverDtoId(T1 messageId, T2 senderId, T3 receiverId) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public MessageSenderReceiverDtoId(MessageSenderReceiverDtoId<T1, T2, T3> other){
        this.messageId = other.getMessageId();
        this.senderId = other.getSenderId();
        this.receiverId = other.getReceiverId();
    }

    public T1 getMessageId() {
        return messageId;
    }

    public T2 getSenderId() {
        return senderId;
    }

    public T3 getReceiverId() {
        return receiverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageSenderReceiverDtoId)) return false;
        MessageSenderReceiverDtoId<?, ?, ?> id = (MessageSenderReceiverDtoId<?, ?, ?>) o;
        return Objects.equals(messageId, id.getMessageId()) && Objects.equals(senderId, id.getSenderId()) &&
                Objects.equals(receiverId, id.getReceiverId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, senderId, receiverId);
    }
}
