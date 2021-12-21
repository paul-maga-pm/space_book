package socialnetwork.domain.entities;

import socialnetwork.utils.containers.OrderedPair;
import socialnetwork.utils.containers.UnorderedPair;

public class ConversationParticipation extends Entity<ConversationParticipationId>{
    public ConversationParticipation(Long participantId, Long conversationId){
        super(new ConversationParticipationId(conversationId, participantId));
    }

    public Long getParticipantId(){
        return super.getId().getUserId();
    }

    public Long getConversationId(){
        return  super.getId().getConversationId();
    }
}
