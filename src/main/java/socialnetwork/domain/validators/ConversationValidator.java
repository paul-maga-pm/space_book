package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Conversation;
import socialnetwork.exceptions.InvalidEntityException;

public class ConversationValidator implements EntityValidator<Long, Conversation>{
    @Override
    public void validate(Conversation conversation) {
        String errorMessage = "";

        if(conversation.getName().equals(""))
            errorMessage += "Name can't be empty!\n";

        if(!errorMessage.equals(""))
            throw new InvalidEntityException(errorMessage);
    }
}
