package socialnetwork.domain.models;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Abstraction of a message in a conversation between users in a network
 */
public class MessageReadModel extends Entity<Long>{
    private User sender;
    private Map<Long, User> mapOfReceivers = new HashMap<>();
    private String text;
    private LocalDateTime date;

    /**
     * Constructor that creates a new message with the given id, from, to, text and date
     * @param id identifier of the message
     * @param sender sender of the message
     * @param text text of the message
     * @param date date of the message
     */
    public MessageReadModel(Long id, User sender, String text, LocalDateTime date){
        super(id);
        this.sender = sender;
        this.text = text;
        this.date = date;
    }

    /**
     * Getter method for from
     * @return sender of the message
     */
    public User getSender() {
        return sender;
    }

    /**
     * Setter method for from
     * @param sender new value for the sender of this message
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Getter method for to
     * @return list of receivers of this message
     */
    public List<User> getReceivers() {
        return mapOfReceivers.values().stream().toList();
    }

    /**
     * Setter method for to
     * @param listOfReceivers new value for the list of receivers of this message
     */
    public void setListOfReceivers(List<User> listOfReceivers) {
        for(var user : listOfReceivers)
            mapOfReceivers.put(user.getId(), user);
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
        if (!(o instanceof MessageReadModel message)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(sender, message.getSender()) &&
                Objects.equals(text, message.getText()) &&
                Objects.equals(date, message.getDate());
    }

    /**
     * Returns hashCode of this MessageReadModel
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sender,text, date);
    }

    /**
     * Parses this MessageReadModel into String format
     * @return String
     */
    @Override
    public String toString() {
        return super.toString() + "\nFrom: " + sender.toString() +
                "\nText: " + text + "\nDate: " + date;
    }

    public boolean isBetween(Long idOfFirstUser, Long idOfSecondUser) {
        return this.sender.getId().equals(idOfFirstUser) && this.mapOfReceivers.containsKey(idOfSecondUser) ||
                this.sender.getId().equals(idOfSecondUser) && this.mapOfReceivers.containsKey(idOfFirstUser);
    }
}
