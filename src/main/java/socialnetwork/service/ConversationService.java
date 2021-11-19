package socialnetwork.service;

import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConversationService {
    private RepositoryInterface<Long, MessageDto> messageDtoRepository;
    private RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverDtoRepository;
    private RepositoryInterface<Long, ReplyDto> replyDtoRepository;
    private RepositoryInterface<Long, User> userRepository;
    private EntityValidatorInterface<Long, Message> messageValidator;

    private static long idAvailable = 0;

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

    private void getIdAvailable(){
        List<MessageDto> messageDtos = messageDtoRepository.getAll();
        if(messageDtos.isEmpty())
            idAvailable = 1;
        else
            idAvailable = messageDtoRepository.getAll().stream().map(messageDto -> messageDto.getId()).max(Long::compare).get() + 1;

    }

    private void setIdAvailable(){
        idAvailable++;
    }

    private Long createMessageId(){
        Long messageId = idAvailable;
        setIdAvailable();
        return messageId;
    }

    private User createSenderUserForMessage(Long senderId){
        Optional<User> existingSenderOptional = userRepository.findById(senderId);
        User sender;
        if(existingSenderOptional.isPresent())
            sender = existingSenderOptional.get();
        else
            sender = new User(-1L, "","");
        return sender;
    }

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

    public List<Message> getConversationBetweenTwoUsersService(Long idOfFirstUser, Long idOfSecondUser){
        List<Message> conversation = new ArrayList<>();

        List<MessageSenderReceiverDto>messagesAndRepliesBetweenTheUsers = messageSenderReceiverDtoRepository.getAll().stream().
                filter(messageSenderReceiverDto -> {
                    return (messageSenderReceiverDto.getId().getSenderId() == idOfFirstUser && messageSenderReceiverDto.getId().getReceiverId() == idOfSecondUser) ||
                            (messageSenderReceiverDto.getId().getSenderId() == idOfSecondUser && messageSenderReceiverDto.getId().getReceiverId() == idOfFirstUser);
                }).collect(Collectors.toList());

        messagesAndRepliesBetweenTheUsers.forEach(messageAndReply -> {
            MessageDto messageDto = messageDtoRepository.findById(messageAndReply.getId().getMessageId()).get();
            Message message = new Message(messageDto.getId(), userRepository.findById(messageAndReply.getId().getSenderId()).get(),
                    messageDto.getText(), messageDto.getDate());
            conversation.add(message);
        });

        conversation.stream().sorted((message1, message2) -> message1.getDate().compareTo(message2.getDate()));

        return conversation;
    }
}
