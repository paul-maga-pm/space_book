package socialnetwork.domain.models;

import java.time.LocalDateTime;

/**
 * Abstraction of a reply in a conversation between users in a network
 */
public class ReplyMessage extends Message{
    private MessageDto messageThatRepliesTo;
    private Long messagedRepliedToId;

    /**
     * Constructor that creates a new replyMessage with the given id, from, to, text and date
     * @param id   identifier of the reply
     * @param from sender of the reply
     * @param text text of the reply
     * @param date date of the reply
     */
    public ReplyMessage(Long id, User from, String text, LocalDateTime date, Long messagedRepliedToId) {
        super(id, from, text, date);
        this.messagedRepliedToId = messagedRepliedToId;
    }

    public ReplyMessage(Long id, User sender, String text, LocalDateTime date, MessageDto messageThatRepliesTo){
        super(id, sender, text, date);
        this.messageThatRepliesTo = messageThatRepliesTo;
        this.messagedRepliedToId = messageThatRepliesTo.getId();
    }

    /**
     * Getter method for messageRepliedTo
     * @return the message that this replyMessage replies to
     */
    public Long getMessagedRepliedToId() {
        return messagedRepliedToId;
    }

    /**
     * Setter method for messageRepliedTo
     * @param messagedRepliedTo new value for the message that this replyMessage replies to
     */
    public void setMessagedRepliedTo(Long messagedRepliedTo) {
        this.messagedRepliedToId = messagedRepliedToId;
    }

    public MessageDto getMessageThatRepliesTo() {
        return messageThatRepliesTo;
    }
}
