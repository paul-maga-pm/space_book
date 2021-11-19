package socialnetwork.domain.models;

import java.time.LocalDateTime;

/**
 * Abstraction of a reply in a conversation between users in a network
 */
public class ReplyMessage extends Message{
    private Message messagedRepliedTo;

    /**
     * Constructor that creates a new replyMessage with the given id, from, to, text and date
     * @param id   identifier of the reply
     * @param from sender of the reply
     * @param text text of the reply
     * @param date date of the reply
     */
    public ReplyMessage(Long id, User from, String text, LocalDateTime date, Message messagedRepliedTo) {
        super(id, from, text, date);
        this.messagedRepliedTo = messagedRepliedTo;
    }

    /**
     * Getter method for messageRepliedTo
     * @return the message that this replyMessage replies to
     */
    public Message getMessagedRepliedTo() {
        return messagedRepliedTo;
    }

    /**
     * Setter method for messageRepliedTo
     * @param messagedRepliedTo new value for the message that this replyMessage replies to
     */
    public void setMessagedRepliedTo(Message messagedRepliedTo) {
        this.messagedRepliedTo = messagedRepliedTo;
    }
}
