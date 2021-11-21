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
import java.util.stream.Collectors;

/**
 * Business Layer for MessageReadModel and ReplyMessageReadModel models
 */
public class ConversationService {
    private RepositoryInterface<Long, MessageDto> messageDtoRepository;
    private RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverDtoRepository;
    private RepositoryInterface<Long, ReplyDto> replyDtoRepository;
    private RepositoryInterface<Long, User> userRepository;
    private EntityValidatorInterface<Long, MessageReadModel> messageValidator;

    private static long idAvailable = 0;
    private MessageWriteModelValidator messageWriteModelValidator;
    private ConversationServiceBoundary conversationServiceBoundary;

    /**
     * Creates a new service that accesses the given repositories and validates the messages by the given validator's rules
     * @param messageDtoRepository repository containing the messageDtos
     * @param messageSenderReceiverDtoRepository repository containing the messageSenderReceiverDtos
     * @param replyDtoRepository repository containing the replyDtos
     * @param userRepository repository containing the users
     * @param messageValidator validator for MessageReadModel model
     */
    public ConversationService(RepositoryInterface<Long, MessageDto> messageDtoRepository,
                               RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverDtoRepository,
                               RepositoryInterface<Long, ReplyDto> replyDtoRepository,
                               RepositoryInterface<Long, User> userRepository,
                               EntityValidatorInterface<Long, MessageReadModel> messageValidator,
                               ConversationServiceBoundary boundary) {
        this.messageDtoRepository = messageDtoRepository;
        this.messageSenderReceiverDtoRepository = messageSenderReceiverDtoRepository;
        this.replyDtoRepository = replyDtoRepository;
        this.userRepository = userRepository;
        this.messageValidator = messageValidator;
        this.conversationServiceBoundary = boundary;
        this.messageWriteModelValidator = new MessageWriteModelValidator(userRepository);
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
        List<MessageReadModel> conversation = new ArrayList<>();

        List<MessageSenderReceiverDto>messagesAndRepliesBetweenTheUsers = messageSenderReceiverDtoRepository.getAll().stream().
                filter(messageSenderReceiverDto -> {
                    return (messageSenderReceiverDto.getId().getSenderId() == idOfFirstUser && messageSenderReceiverDto.getId().getReceiverId() == idOfSecondUser) ||
                            (messageSenderReceiverDto.getId().getSenderId() == idOfSecondUser && messageSenderReceiverDto.getId().getReceiverId() == idOfFirstUser);
                }).collect(Collectors.toList());

        Map<Long, User> mapBetweenMessageDtoIdAndSender = new HashMap<>();

        for(var msg : messageSenderReceiverDtoRepository.getAll()) {
            Long senderId = msg.getId().getSenderId();
            User sender = userRepository.findById(senderId).get();
            mapBetweenMessageDtoIdAndSender.put(msg.getId().getMessageId(),
                    sender);
        }

        messagesAndRepliesBetweenTheUsers.forEach(msg -> {
            MessageReadModel message;
            Long messageId = msg.getId().getMessageId();
            Long senderId = msg.getId().getSenderId();
            Optional<ReplyDto> optionalOfReply = replyDtoRepository.findById(messageId);
            Optional<MessageDto> messageDto = messageDtoRepository.findById(messageId);
            String textOfMessage = messageDto.get().getText();
            LocalDateTime date = messageDto.get().getDate();
            User sender = userRepository.findById(senderId).get();
            if(optionalOfReply.isPresent()) {
                Long idOfMessageThatIsRepliedTo = optionalOfReply.get().getIdOfMessageThatIsRepliedTo();
                MessageDto messageDtoThatRepliesTo = messageDtoRepository.findById(idOfMessageThatIsRepliedTo).get();
                User receiverOfReply = mapBetweenMessageDtoIdAndSender.get(idOfMessageThatIsRepliedTo);
                MessageReadModel messageThatRepliesTo = new MessageReadModel(idOfMessageThatIsRepliedTo,
                        receiverOfReply,
                        messageDtoThatRepliesTo.getText(),
                        messageDtoThatRepliesTo.getDate());
                message = new ReplyMessageReadModel(messageId, sender, textOfMessage, date, messageThatRepliesTo);
            }
            else
                message = new MessageReadModel(messageId, sender, textOfMessage, date);
            conversation.add(message);
        });
        conversation.sort(Comparator.comparing(MessageReadModel::getDate));
        return conversation;
    }

    public void removeAllConversationsOfUserService(Long idOfUser) {
        conversationServiceBoundary.removeAllConversationsOfUser(idOfUser);
    }
}
