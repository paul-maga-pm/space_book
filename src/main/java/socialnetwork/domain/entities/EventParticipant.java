package socialnetwork.domain.entities;

import socialnetwork.utils.containers.UnorderedPair;

import java.util.Objects;

public class EventParticipant extends Entity<UnorderedPair<Long, Long>>{
    private NotificationStatus notificationStatus;

    public EventParticipant(Long userId, Long eventId) {
        super(new UnorderedPair<>(userId, eventId));
    }

    public EventParticipant(Long userId, Long eventId, NotificationStatus notificationStatus) {
        super(new UnorderedPair<>(userId, eventId));
        this.notificationStatus = notificationStatus;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
