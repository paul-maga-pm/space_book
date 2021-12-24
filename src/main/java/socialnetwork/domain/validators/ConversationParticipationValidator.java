package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Conversation;
import socialnetwork.domain.entities.ConversationParticipation;
import socialnetwork.domain.entities.ConversationParticipationId;
import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.Repository;

public class ConversationParticipationValidator
        implements EntityValidator<ConversationParticipationId, ConversationParticipation> {

    private Repository<Long, User> userRepository;
    private Repository<Long, Conversation> conversationRepository;

    public ConversationParticipationValidator(Repository<Long, User> userRepository,
                                              Repository<Long, Conversation> conversationRepository){
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
    }

    @Override
    public void validate(ConversationParticipation entity) {
        String errorMessage = "";

        if(conversationRepository.findById(entity.getConversationId()).isEmpty())
            errorMessage += "Conversation doesn't exist!\n";

        if(userRepository.findById(entity.getParticipantId()).isEmpty())
            errorMessage += "Participant doesn't exist!\n";

        if(!errorMessage.equals(""))
            throw new InvalidEntityException(errorMessage);
    }
}
