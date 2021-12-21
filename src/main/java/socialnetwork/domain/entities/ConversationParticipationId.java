package socialnetwork.domain.entities;

import socialnetwork.utils.containers.OrderedPair;

import java.util.Objects;

public class ConversationParticipationId {
    private Long conversationId;
    private Long userId;

    public ConversationParticipationId(Long conversationId, Long userId){
        this.userId = userId;
        this.conversationId = conversationId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationParticipationId)) return false;
        ConversationParticipationId that = (ConversationParticipationId) o;
        return Objects.equals(conversationId, that.conversationId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, userId);
    }
}
