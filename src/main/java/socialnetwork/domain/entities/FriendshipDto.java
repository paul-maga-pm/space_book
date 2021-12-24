package socialnetwork.domain.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FriendshipDto {
    private User friend;
    private LocalDateTime friendshipDate;

    public FriendshipDto(User friend, LocalDateTime friendshipDate) {
        this.friend = friend;
        this.friendshipDate = friendshipDate;
    }

    public User getFriend() {
        return friend;
    }

    public LocalDateTime getFriendshipDate() {
        return friendshipDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendshipDto)) return false;
        FriendshipDto that = (FriendshipDto) o;
        return Objects.equals(friend, that.friend) &&
                Objects.equals(friendshipDate, that.friendshipDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friend, friendshipDate);
    }

    @Override
    public String toString() {
        return friend.toString() + " " + friendshipDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:SS"));
    }
}
