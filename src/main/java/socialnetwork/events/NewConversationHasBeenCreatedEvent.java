package socialnetwork.events;

import socialnetwork.domain.entities.ConversationDto;

public class NewConversationHasBeenCreatedEvent {
    private ConversationDto conversation;

    public NewConversationHasBeenCreatedEvent(ConversationDto conversation) {
        this.conversation = conversation;
    }

    public ConversationDto getConversation() {
        return conversation;
    }
}
