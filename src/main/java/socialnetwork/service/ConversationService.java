package socialnetwork.service;

import socialnetwork.boundary.ConversationServiceBoundary;
import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.MessageWriteModelValidator;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Business Layer for MessageReadModel and ReplyMessageReadModel models
 */
public class ConversationService {
    private ConversationServiceBoundary conversationServiceBoundary;

    /**
     * Creates a new service that accesses the MessageReadModels through the given boundary
     */
    public ConversationService(ConversationServiceBoundary boundary) {
        this.conversationServiceBoundary = boundary;
    }

    /**
     * Add a new message
     * @param senderId identifier of sender
     * @param receiverIds identifiers of receivers
     * @param text content of the message
     * @param date date when the message was sent
     */
    public void sendMessageFromUserToService(Long senderId, List<Long> receiverIds, String text, LocalDateTime date){
        MessageWriteModel messageWriteModel = new MessageWriteModel(senderId, text, date);
        messageWriteModel.setIdListOfReceivers(receiverIds);
        conversationServiceBoundary.sendMessage(messageWriteModel);
    }

    /**
     * Add a new reply
     * @param messageRepliedToId identifier of the message that we reply to
     * @param senderId identifier of the sender
     * @param text content of the reply
     * @param date date when the reply was sent
     * @throws InvalidEntityException if there is no message with messageRepliedToId or
     *                                if senderId is not part of the receivers of the message with messageRepliedToId
     */
    public void replyToMessageService(Long messageRepliedToId, Long senderId, String text, LocalDateTime date){
        if(conversationServiceBoundary.isReplyMessage(messageRepliedToId))
            throw new InvalidEntityException("You can't reply to another reply");
        ReplyMessageWriteModel replyMessageWriteModel = new ReplyMessageWriteModel(messageRepliedToId,
                senderId,
                text,
                date);
        conversationServiceBoundary.sendReplyMessage(replyMessageWriteModel);
    }

    /**
     * Get conversation between two users
     * @param idOfFirstUser identifier of the first user that is part of the conversation
     * @param idOfSecondUser identifier of the second user that is part of the conversation
     * @return the conversation (list of messages) between the two users
     */
    public List<MessageReadModel> getConversationBetweenTwoUsersService(Long idOfFirstUser, Long idOfSecondUser){
        List<MessageReadModel> allMessages = conversationServiceBoundary.getAllMessageReadModels();
        Predicate<MessageReadModel> filterPredicate = m -> m.isBetween(idOfFirstUser, idOfSecondUser);
        List<MessageReadModel> conversation = allMessages.stream().filter(filterPredicate).toList();
        conversation.sort(Comparator.comparing(MessageReadModel::getDate));
        return conversation;
    }

    public void removeAllConversationsOfUserService(Long idOfUser) {
        conversationServiceBoundary.removeAllConversationsOfUser(idOfUser);
    }
}
