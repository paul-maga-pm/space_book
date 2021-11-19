package socialnetwork.domain.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstraction of a message in a conversation between users in a network
 */
public class Message extends Entity<Long>{
    private User from;
    private List<User> to = new ArrayList<>();
    private String text;
    private LocalDateTime date;

    /**
     * Constructor that creates a new message with the given id, from, to, text and date
     * @param id identifier of the message
     * @param from sender of the message
     * @param text text of the message
     * @param date date of the message
     */
    public Message(Long id, User from, String text, LocalDateTime date){
        super(id);
        this.from = from;
        this.text = text;
        this.date = date;
    }

    /**
     * Getter method for from
     * @return sender of the message
     */
    public User getFrom() {
        return from;
    }

    /**
     * Setter method for from
     * @param from new value for the sender of this message
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * Getter method for to
     * @return list of receivers of this message
     */
    public List<User> getTo() {
        return to;
    }

    /**
     * Setter method for to
     * @param to new value for the list of receivers of this message
     */
    public void setTo(List<User> to) {
        this.to = to;
    }

    /**
     * Getter method for text
     * @return text of the message
     */
    public String getText() {
        return text;
    }

    /**
     * Setter method for text
     * @param text new value for the text of this message
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Getter method for date
     * @return date when the message was sent
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Setter method for date
     * @param date new value for the date when the message was sent
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Checks if this and o are equal by value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(from, message.getFrom()) &&
                Objects.equals(to, message.getTo()) &&
                Objects.equals(text, message.getText()) &&
                Objects.equals(date, message.getDate());
    }

    /**
     * Returns hashCode of this Message
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to, text, date);
    }

    /**
     * Parses this Message into String format
     * @return String
     */
    @Override
    public String toString() {
        return super.toString() + "\nFrom: " + from.toString() +
                "\nText: " + text + "\nDate: " + date;
    }

}
