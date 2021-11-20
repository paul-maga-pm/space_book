package socialnetwork.service;

import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bussines Layer for Message and ReplyMessage models
 */
public class ConversationService {
    private RepositoryInterface<Long, MessageDto> messageDtoRepository;
    private RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverDtoRepository;
    private RepositoryInterface<Long, ReplyDto> replyDtoRepository;
    private RepositoryInterface<Long, User> userRepository;
    private EntityValidatorInterface<Long, Message> messageValidator;

    private static long idAvailable = 0;

    /**
     * Creates a new service that accesses the given repositories and validates the messages by the given validator's rules
     * @param messageDtoRepository repository containing the messageDtos
     * @param messageSenderReceiverDtoRepository repository containing the messageSenderReceiverDtos
     * @param replyDtoRepository repository containing the replyDtos
     * @param userRepository repository containing the users
     * @param messageValidator validator for Message model
     */
    public ConversationService(RepositoryInterface<Long, MessageDto> messageDtoRepository,
                               RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverDtoRepository,
                               RepositoryInterface<Long, ReplyDto> replyDtoRepository,
                               RepositoryInterface<Long, User> userRepository,
                               EntityValidatorInterface<Long, Message> messageValidator) {
        this.messageDtoRepository = messageDtoRepository;
        this.messageSenderReceiverDtoRepository = messageSenderReceiverDtoRepository;
        this.replyDtoRepository = replyDtoRepository;
        this.userRepository = userRepository;
        this.messageValidator = messageValidator;

        getIdAvailable();
    }

    /**
     * finds an id that can be used in the future for a new Message object
     */
    private void getIdAvailable(){
        List<MessageDto> messageDtos = messageDtoRepository.getAll();
        if(messageDtos.isEmpty())
            idAvailable = 1;
        else
            idAvailable = messageDtoRepository.getAll().stream().map(messageDto -> messageDto.getId()).max(Long::compare).get() + 1;

    }

    /**
     * sets idAvailable to the next free value for a message id
     */
    private void setIdAvailable(){
        idAvailable++;
    }

    /**
     * gets and available id for a new Message object
     * @return identifier of a new message
     */
    private Long createMessageId(){
        Long messageId = idAvailable;
        setIdAvailable();
        return messageId;
    }

    /**
     * creates a user based on whether it exists or not
     * @param senderId identifier of a user
     * @return the user with the  given id (if it exists)
     *         a mockup user otherwise
     */
    private User createSenderUserForMessage(Long senderId){
        Optional<User> existingSenderOptional = userRepository.findById(senderId);
        User sender;
        if(existingSenderOptional.isPresent())
            sender = existingSenderOptional.get();
        else
            sender = new User(-1L, "","");
        return sender;
    }

    /**
     * creates a list of users based on a list of their ids
     * @param receiverIds list of identifier of users
     * @return the list of users
     */
    private List<User> createReceiverUsersListForMessage(List<Long> receiverIds){
        List<User> receivers = new ArrayList<>();
        for(Long receiverId: receiverIds){
            Optional<User> existingReceiverOptional = userRepository.findById(receiverId);
            if(existingReceiverOptional.isPresent())
                receivers.add(existingReceiverOptional.get());
            else
                receivers.add(new User(-1L, "", ""));
        }
        return receivers;
    }

    /**
     * Add a new message
     * @param senderId identifier of sender
     * @param receiverIds identifiers of receivers
     * @param text content of the message
     * @param date date when the message was sent
     */
    public void sendMessageFromUserToService(Long senderId, List<Long> receiverIds, String text, LocalDateTime date){
        Long messageId = createMessageId();
        User sender = createSenderUserForMessage(senderId);
        List<User> receivers = createReceiverUsersListForMessage(receiverIds);

        Message message = new Message(messageId, sender, text, date);
        message.setTo(receivers);
        messageValidator.validate(message);

        messageDtoRepository.save(new MessageDto(message.getId(), text, date));
        receiverIds.forEach(receiverId -> {
            messageSenderReceiverDtoRepository.save(new MessageSenderReceiverDto(message.getId(), senderId, receiverId));
        });
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
        Optional<MessageDto> messageRepliedToDto = messageDtoRepository.findById(messageRepliedToId);
        if(messageRepliedToDto.isEmpty())
            throw new InvalidEntityException("Message that is replied to must exist");

        List<MessageSenderReceiverDto> messageSenderReceiverDtos = messageSenderReceiverDtoRepository.getAll().stream().
                filter(messageSenderReceiverDto -> messageSenderReceiverDto.getId().getMessageId() == messageRepliedToId).
                collect(Collectors.toList());

        List<MessageSenderReceiverDto> verifyIfSenderIdIsPartOfInitialReceivers = messageSenderReceiverDtos.stream().
                filter(messageSenderReceiverDto -> messageSenderReceiverDto.getId().getReceiverId() == senderId).
                collect(Collectors.toList());
        if(verifyIfSenderIdIsPartOfInitialReceivers.isEmpty())
            throw new InvalidEntityException("Sender must be part of the receivers of the message he wants to reply to");

        List<Long> receiverIdsOfReplyMessage = new ArrayList<>();
        receiverIdsOfReplyMessage.add(messageSenderReceiverDtos.get(0).getId().getSenderId());
        messageSenderReceiverDtos.forEach(messageSenderReceiverDto -> {
            Long receiverId = messageSenderReceiverDto.getId().getReceiverId();
            if(receiverId != senderId)
                receiverIdsOfReplyMessage.add(receiverId);
        });

        Long replyId = createMessageId();
        User replier = createSenderUserForMessage(senderId);
        List<User> replyReceivers = createReceiverUsersListForMessage(receiverIdsOfReplyMessage);
        ReplyMessage reply = new ReplyMessage(replyId, replier, text, date, messageRepliedToId);
        reply.setTo(replyReceivers);

        messageValidator.validate(reply);

        messageDtoRepository.save(new MessageDto(replyId, text, date));
        receiverIdsOfReplyMessage.forEach(receiverId -> {
            messageSenderReceiverDtoRepository.save(new MessageSenderReceiverDto(replyId, senderId, receiverId));
        });
        replyDtoRepository.save(new ReplyDto(replyId, messageRepliedToId));
    }

    /**
     * Get conversation between two users
     * @param idOfFirstUser identifier of the first user that is part of the conversation
     * @param idOfSecondUser identifier of the second user that is part of the conversation
     * @return the conversation (list of messages) between the two users
     */
    public List<Message> getConversationBetweenTwoUsersService(Long idOfFirstUser, Long idOfSecondUser){
        List<Message> conversation = new ArrayList<>();

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
            Message message;
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
                Message messageThatRepliesTo = new Message(idOfMessageThatIsRepliedTo,
                        receiverOfReply,
                        messageDtoThatRepliesTo.getText(),
                        messageDtoThatRepliesTo.getDate());
                message = new ReplyMessage(messageId, sender, textOfMessage, date, messageThatRepliesTo);
            }
            else
                message = new Message(messageId, sender, textOfMessage, date);
            conversation.add(message);
        });
        conversation.sort(Comparator.comparing(Message::getDate));
        return conversation;
    }

    /**
     * @return all messageDto objects
     */
    public List<MessageDto> getAllMessageDtoService(){
        return messageDtoRepository.getAll();
    }

    /**
     * @return all messageSenderReceiverDto objects
     */
    public List<MessageSenderReceiverDto> getAllMessageSenderReceiverDtoService(){
        return messageSenderReceiverDtoRepository.getAll();
    }

    /**
     * @return all replyDto objects
     */
    public List<ReplyDto> getAllReplyDtoService(){
        return replyDtoRepository.getAll();
    }
}
