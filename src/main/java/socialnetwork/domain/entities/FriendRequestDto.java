package socialnetwork.domain.entities;

import java.util.Objects;

public class FriendRequestDto{
    private FriendRequest friendRequest;
    private User sender;

    public FriendRequestDto(FriendRequest friendRequest, User sender) {
        this.friendRequest = friendRequest;
        this.sender = sender;
    }

    public FriendRequest getFriendRequest() {
        return friendRequest;
    }

    public User getSender() {
        return sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendRequestDto)) return false;
        FriendRequestDto that = (FriendRequestDto) o;
        return Objects.equals(friendRequest, that.friendRequest) &&
                Objects.equals(sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendRequest, sender);
    }
}
