package socialnetwork.domain.models;

import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstraction of a friendship relationship between two users in the network
 */
public class Friendship extends Entity<UnorderedPair<Long, Long>>{
    private LocalDateTime date;

    /**
     * Constructor that creates a new relationship between the users with the given id
     * @param idOfFirstUser identifier of first user
     * @param idOfSecondUser identifier of second user
     */
    public Friendship(Long idOfFirstUser, Long idOfSecondUser){
        super(new UnorderedPair<>(idOfFirstUser, idOfSecondUser));
    }

    /**
     * Constructor that creates a new relationship between the users with the given id
     * @param idOfFirstUser identifier of first user
     * @param idOfSecondUser identifier of second user
     * @param date LocalDateTime when the two users became friends
     */

    public Friendship(Long idOfFirstUser, Long idOfSecondUser, LocalDateTime date){
        super(new UnorderedPair<>(idOfFirstUser, idOfSecondUser));
        this.date = date;
    }

    /**
     * Getter method for date of creation of friendship
     * @return LocalDateTime representing the date of creation of the friendship
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Checks if the user is in the friendship
     * @param idOfUser identifier of the user we want to check if he is in the relationship
     * @return true if user is in friendship, false otherwise
     */
    public boolean hasUser(Long idOfUser){
        return getId().first.compareTo(idOfUser) == 0 ||
                getId().second.compareTo(idOfUser) == 0;
    }

    /**
     * Checks if this Friendship and o are equal by value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship)) return false;
        if (!super.equals(o)) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(date, that.date);
    }

    /**
     * Returns hashCode of this
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date);
    }

    /**
     * Parses this into String format
     * @return String in format "Id of first user: {idOfFirstUser}, Id of second user {idOfSecondUser}"
     */
    @Override
    public String toString() {
        return "Id of first user: " + getId().first +
                ", Id of second user: " + getId().second +
                ", Date: " + date.toString();
    }
}
