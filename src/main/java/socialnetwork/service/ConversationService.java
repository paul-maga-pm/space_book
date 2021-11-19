package socialnetwork.service;

import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.repository.RepositoryInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private Message createMessage(Long senderId, List<Long> receiverIds, String text, LocalDateTime date){
        Long messageId = idAvailable;
        setIdAvailable();
        Optional<User> existingSenderOptional = userRepository.findById(senderId);
        User sender;
        if(existingSenderOptional.isPresent())
            sender = existingSenderOptional.get();
        else
            sender = new User(-1L, "","");
        List<User> receivers = new ArrayList<>();
        for(Long receiverId: receiverIds){
            Optional<User> existingReceiverOptional = userRepository.findById(receiverId);
            if(existingReceiverOptional.isPresent())
                receivers.add(existingReceiverOptional.get());
            else
                receivers.add(new User(-1L, "", ""));
        }

        Message message = new Message(messageId, sender, text, date);
        message.setTo(receivers);

        return message;
    }

    public void sendMessageFromUserTo(Long senderId, List<Long> receiverIds, String text, LocalDateTime date){
        Message message = createMessage(senderId, receiverIds, text, date);
        messageValidator.validate(message);
        messageDtoRepository.save(new MessageDto(message.getId(), text, date));
        receiverIds.forEach(receiverId -> {
            messageSenderReceiverDtoRepository.save(new MessageSenderReceiverDto(message.getId(), senderId, receiverId));
        });
    }
}
