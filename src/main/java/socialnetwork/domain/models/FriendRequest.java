package socialnetwork.domain.models;

import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstraction of a friend request sent from one user to another user in the network
 */
public class FriendRequest extends Entity<UnorderedPair<Long, Long>>{
    private Status status;
    private LocalDateTime date;

    /**
     * Constructor that creates a new friend request between the users with the given id
     * @param idOfFirstUser identifier of the first user
     * @param idOfSecondUser identifier of the second user
     * @param status current status of the friend request (pending, approved, rejected)
     */
    public FriendRequest(Long idOfFirstUser, Long idOfSecondUser, Status status) {
        super(new UnorderedPair<>(idOfFirstUser, idOfSecondUser));
        this.status = status;
    }

    public FriendRequest(Long idOfFirstUser, Long idOfSecondUser, Status status, LocalDateTime date) {
        super(new UnorderedPair<>(idOfFirstUser, idOfSecondUser));
        this.status = status;
        this.date = date;
    }

    /**
     * Getter method for status
     * @return current status of the friend request
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Setter method for status
     * @param status new value for the status of this friend request
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Checks if the user is in the friendRequest
     * @param idOfUser identifier of the user we want to check if he is in the friendRequest
     * @return true if user is in friendRequest, false otherwise
     */
    public boolean hasUser(Long idOfUser){
        return getId().first.equals(idOfUser) || getId().second.equals(idOfUser);
    }

    /**
     * Checks if this FriendRequest and o are equal by value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendRequest)) return false;
        if (!super.equals(o)) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(status, that.getStatus());
    }

    /**
     * Returns hashCode of this
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }

    /**
     * Parses this into String format
     * @return String in format "From: {idOfFirstUser}, To: {idOfSecondUser}, Status: {status}"
     */
    @Override
    public String toString() {
        return "From: " + getId().first +
                ", To: " + getId().second +
                ", Status: " + status.toString();
    }
}
