package socialnetwork.domain.models;

import socialnetwork.utils.containers.UnorderedPair;

import java.util.Objects;

/**
 * Abstraction of a friend request sent from one user to another user in the network
 */
public class FriendRequest extends Entity<UnorderedPair<Long, Long>>{
    private String status;

    /**
     * Constructor that creates a new friend request between the users with the given id
     * @param idOfFirstUser identifier of the first user
     * @param idOfSecondUser identifier of the second user
     * @param status current status of the friend request (pending, approved, denied)
     */
    public FriendRequest(Long idOfFirstUser, Long idOfSecondUser, String status) {
        super(new UnorderedPair<>(idOfFirstUser, idOfSecondUser));
        this.status = status;
    }

    /**
     * Getter method for status
     * @return current status of the friend request
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter method for status
     * @param status new value for the status of this friend request
     */
    public void setStatus(String status) {
        this.status = status;
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
     * @return String in format "Id of first user: {idOfFirstUser}, Id of second user: {idOfSecondUser}, Status: {status}"
     */
    @Override
    public String toString() {
        return "Id of first user: " + getId().first +
                ", Id of second user: " + getId().second +
                ", Status: " + status;
    }
}
