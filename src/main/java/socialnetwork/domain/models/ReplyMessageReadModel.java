package socialnetwork.domain.models;

import java.time.LocalDateTime;

/**
 * Abstraction of a reply in a conversation between users in a network
 */
public class ReplyMessageReadModel extends MessageReadModel {
    private MessageReadModel messageThatRepliesTo;

    public ReplyMessageReadModel(Long id,
                                 User sender,
                                 String text,
                                 LocalDateTime date,
                                 MessageReadModel messageThatRepliesTo){
        super(id, sender, text, date);
        this.messageThatRepliesTo = messageThatRepliesTo;
    }

    public MessageReadModel getMessageThatRepliesTo() {
        return messageThatRepliesTo;
    }

    @Override
    public String toString() {
        return "REPLY TO " + messageThatRepliesTo.toString() + "\n" + super.toString();
    }
}
