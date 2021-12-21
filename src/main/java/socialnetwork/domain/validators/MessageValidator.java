package socialnetwork.domain.validators;

import socialnetwork.domain.entities.*;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.containers.OrderedPair;

public class MessageValidator implements EntityValidator<Long, Message> {
    private Repository<ConversationParticipationId, ConversationParticipation> participationRepository;

    public MessageValidator(Repository<ConversationParticipationId, ConversationParticipation> participationRepository){
        this.participationRepository = participationRepository;
    }

    @Override
    public void validate(Message message) {
        String errorMessage = "";

        ConversationParticipationId id = new ConversationParticipationId(message.getConversationId(),
                message.getSenderId());
        if(participationRepository.findById(id).isEmpty())
            errorMessage += "User doesn't participate in the conversation";

        if(message.getText().equals(""))
            errorMessage += "Message can't be empty!";

        if(!errorMessage.equals(""))
            throw new InvalidEntityException(errorMessage);
    }
}
