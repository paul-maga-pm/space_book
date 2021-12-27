package socialnetwork.domain.entities;

import socialnetwork.utils.containers.UnorderedPair;

import java.util.Objects;

public class EventParticipant extends Entity<UnorderedPair<Long, Long>>{

    public EventParticipant(Long userId, Long eventId) {
        super(new UnorderedPair<>(userId, eventId));
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
