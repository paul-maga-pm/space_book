package socialnetwork.boundary;

import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.MessageWriteModelValidator;
import socialnetwork.domain.validators.ReplyMessageWriteModelValidator;
import socialnetwork.repository.RepositoryInterface;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

public class ConversationServiceBoundary {
    private RepositoryInterface<Long, MessageDto> messageDtoRepository;
    private RepositoryInterface<Long, ReplyDto> replyDtoRepository;
    private RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverRepository;
    private RepositoryInterface<Long, User> userRepository;
    private static long idAvailable = 0;
    private MessageWriteModelValidator messageWriteModelValidator;
    private ReplyMessageWriteModelValidator replyMessageWriteModelValidator;

    public ConversationServiceBoundary(
            RepositoryInterface<Long, User> userRepository,
            RepositoryInterface<Long, MessageDto> messageDtoRepository,
            RepositoryInterface<Long, ReplyDto> replyDtoRepository,
            RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto>
                messageSenderReceiverRepository) {

        this.userRepository = userRepository;
        this.messageDtoRepository = messageDtoRepository;
        this.replyDtoRepository = replyDtoRepository;
        this.messageSenderReceiverRepository = messageSenderReceiverRepository;
        messageWriteModelValidator = new MessageWriteModelValidator(userRepository);
        replyMessageWriteModelValidator = new ReplyMessageWriteModelValidator(messageDtoRepository,
                messageSenderReceiverRepository);
        setAvailableIdWithinExistingIds();
    }

    public List<MessageReadModel> getAllMessageReadModels(){
        Map<Long, User> mapBetweenMessageDtoIdAndSender = getMapBetweenMessageIdAndSender();
        List<MessageReadModel> allMessageReadModel = new ArrayList<>();

        for(var messageDto : messageDtoRepository.getAll()){
            MessageReadModel messageReadModelThatWillBeAdded = getMessageReadModelFrom(mapBetweenMessageDtoIdAndSender,
                    messageDto);
            allMessageReadModel.add(messageReadModelThatWillBeAdded);
        }
        return allMessageReadModel;
    }

    private MessageReadModel getMessageReadModelFrom(Map<Long, User> mapBetweenMessageDtoIdAndSender,
                                                     MessageDto messageDto) {
        MessageReadModel messageReadModelThatWillBeAdded;

        String textOfMessage = messageDto.getText();
        LocalDateTime dateOfMessage = messageDto.getDate();
        User senderOfMessage = mapBetweenMessageDtoIdAndSender.get(messageDto.getId());
        if(isMessageDtoReply(messageDto)){
            MessageReadModel messageThatRepliesTo = getMessageThatRepliesTo(mapBetweenMessageDtoIdAndSender,
                    messageDto);
            messageReadModelThatWillBeAdded = new ReplyMessageReadModel(messageDto.getId(),
                    senderOfMessage,
                    textOfMessage,
                    dateOfMessage,
                    messageThatRepliesTo);
        } else messageReadModelThatWillBeAdded = new MessageReadModel(messageDto.getId(),
                senderOfMessage,
                textOfMessage,
                dateOfMessage);
        messageReadModelThatWillBeAdded.setListOfReceivers(findAllReceiversOfMessageWithId(messageDto.getId()));
        return messageReadModelThatWillBeAdded;
    }

    private MessageReadModel getMessageThatRepliesTo(Map<Long, User> mapBetweenMessageDtoIdAndSender,
                                                     MessageDto reply) {
        Long idOfMessageThatIsRepliedTo = getIdOfMessageThatRepliesTo(reply);
        User senderOfMessageThatIsRepliedTo = mapBetweenMessageDtoIdAndSender.get(idOfMessageThatIsRepliedTo);
        String textOfMessageThatIsRepliedTo = getTextOfMessageThatRepliesTo(reply);
        LocalDateTime dateOfMessageThatIsRepliedTo = getDateOfMessageThatRepliesTo(reply);
        return  new MessageReadModel(
                idOfMessageThatIsRepliedTo,
                senderOfMessageThatIsRepliedTo,
                textOfMessageThatIsRepliedTo,
                dateOfMessageThatIsRepliedTo
        );
    }

    private LocalDateTime getDateOfMessageThatRepliesTo(MessageDto reply) {
        Long id = getIdOfMessageThatRepliesTo(reply);
        return messageDtoRepository.findById(id)
                .get()
                .getDate();
    }

    private String getTextOfMessageThatRepliesTo(MessageDto reply) {
        Long id = getIdOfMessageThatRepliesTo(reply);
        return messageDtoRepository.findById(id)
                .get()
                .getText();
    }

    private Long getIdOfMessageThatRepliesTo(MessageDto messageDto) {
        return replyDtoRepository.findById(messageDto.getId()).get().getIdOfMessageThatIsRepliedTo();
    }

    private boolean isMessageDtoReply(MessageDto messageDto) {
        return replyDtoRepository.findById(messageDto.getId()).isPresent();
    }

    private Map<Long, User> getMapBetweenMessageIdAndSender() {
        Map<Long, User> mapBetweenMessageDtoIdAndSender = new HashMap<>();
        for(var msg : messageSenderReceiverRepository.getAll()) {
            Long senderId = msg.getId().getSenderId();
            User sender = userRepository.findById(senderId).get();
            mapBetweenMessageDtoIdAndSender.put(msg.getId().getMessageId(),
                    sender);
        }
        return mapBetweenMessageDtoIdAndSender;
    }

    private List<User> findAllReceiversOfMessageWithId(Long idOfMessage) {
        List<User> receivers = new ArrayList<>();

        for(var dto : messageSenderReceiverRepository.getAll())
            if(dto.isOfMessage(idOfMessage)){
                Long idOfReceiver = dto.getIdOfReceiver();
                User receiver = userRepository.findById(idOfReceiver).get();
                receivers.add(receiver);
            }
        return receivers;
    }

    public void sendMessage(MessageWriteModel message) {
        messageWriteModelValidator.validate(message);
        Long idOfMessage = createMessageWriteModelId();
        messageDtoRepository.save(new MessageDto(idOfMessage, message.getText(), message.getDate()));
        saveMessageSenderReceiverDtoOfListOfReceiversOf(message, idOfMessage);
    }

    private void saveMessageSenderReceiverDtoOfListOfReceiversOf(MessageWriteModel message, Long idOfMessage) {
        for (Long idOfReceiver : message.getIdListOfReceivers())
            saveMessageSenderReceiverDtoBetween(message.getIdOfSender(), idOfMessage, idOfReceiver);
    }

    private void saveMessageSenderReceiverDtoBetween(Long idOfSender, Long idOfMessage, Long idOfReceiver) {
        MessageSenderReceiverDto dto = new MessageSenderReceiverDto(idOfMessage,
                idOfSender,
                idOfReceiver);
        messageSenderReceiverRepository.save(dto);
    }

    public void sendReplyMessage(ReplyMessageWriteModel replyMessage){
        List<Long> receiversIdList = getIdOfUsersThatWillReceiveTheReply(replyMessage);
        replyMessage.setIdListOfReceivers(receiversIdList);
        replyMessageWriteModelValidator.validate(replyMessage);
        sendMessage(replyMessage);
        ReplyDto dto = new ReplyDto(idAvailable - 1, replyMessage.getIdOfMessageThatRepliesTo());
        replyDtoRepository.save(dto);
    }

    public void removeAllConversationsOfUser(Long idOfUser) {
        for(MessageSenderReceiverDto messageSenderReceiverDto : messageSenderReceiverRepository.getAll()){
            boolean messageWasSentOrReceivedByTheUser = messageSenderReceiverDto.messageIsSentOrReceivedByUser(idOfUser);
            if(messageWasSentOrReceivedByTheUser)
                removeMessageAndAllRepliesRelatedToMessage(messageSenderReceiverDto);
        }
    }

    private void removeMessageAndAllRepliesRelatedToMessage(MessageSenderReceiverDto messageSenderReceiverDto) {
        removeAllRepliesRelatedToMessage(messageSenderReceiverDto);
        messageSenderReceiverRepository.remove(messageSenderReceiverDto.getId());
        messageDtoRepository.remove(messageSenderReceiverDto.getId().getMessageId());
    }

    private void removeAllRepliesRelatedToMessage(MessageSenderReceiverDto messageSenderReceiverDto) {
        for(ReplyDto replyDto : replyDtoRepository.getAll()){
            removeReplyIfMessageSenderReceiverIsRelated(messageSenderReceiverDto, replyDto);
        }
    }

    private void removeReplyIfMessageSenderReceiverIsRelated
            (MessageSenderReceiverDto messageSenderReceiverDto, ReplyDto replyDto) {
        Long idOfMessage = messageSenderReceiverDto.getId().getMessageId();
        boolean messageWasAReplyOrWasRepliedTo =
                replyDto.messageIsAReplyOrIsRepliedTo(idOfMessage);
        if(messageWasAReplyOrWasRepliedTo)
            replyDtoRepository.remove(replyDto.getId());
    }

    private List<Long> getIdOfUsersThatWillReceiveTheReply(ReplyMessageWriteModel replyMessage) {
        Long idOfSenderOfReply = replyMessage.getIdOfSender();
        Long idOfMessageThatRepliesTo = replyMessage.getIdOfMessageThatRepliesTo();
        List<Long> receiversIdList = new ArrayList<>();
        for(MessageSenderReceiverDto dto : messageSenderReceiverRepository.getAll())
            if(dto.isOfMessage(idOfMessageThatRepliesTo)){
                Long idOfReceiver = getIdOfReceiverOfDtoIfMessageIsNotReceivedByUserOrIdOfSenderOtherwise
                        (idOfSenderOfReply, dto);
                receiversIdList.add(idOfReceiver);
            }
        return receiversIdList;
    }

    private Long getIdOfReceiverOfDtoIfMessageIsNotReceivedByUserOrIdOfSenderOtherwise
            (Long idOfUser, MessageSenderReceiverDto dto) {
        boolean isDtoReceivedByUser = dto.isMessageReceivedBy(idOfUser);
        if (!isDtoReceivedByUser)
            return dto.getId().getReceiverId();
        return dto.getId().getSenderId();
    }

    private void setAvailableIdWithinExistingIds(){
        List<MessageDto> messageDtoList = messageDtoRepository.getAll();
        if(messageDtoList.isEmpty())
            idAvailable = 1;
        else
            idAvailable = messageDtoRepository.getAll()
                    .stream()
                    .map(dto -> dto.getId()).max(Long::compare).get() + 1;
    }

    private Long createMessageWriteModelId(){
        Long messageId = idAvailable;
        setIdAvailable();
        return messageId;
    }

    private void setIdAvailable(){
        idAvailable++;
    }
}