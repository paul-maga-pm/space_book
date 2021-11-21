package socialnetwork.boundary;

import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.MessageWriteModelValidator;
import socialnetwork.domain.validators.ReplyMessageWriteModelValidator;
import socialnetwork.repository.RepositoryInterface;

import java.util.ArrayList;
import java.util.List;

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

        this.messageDtoRepository = messageDtoRepository;
        this.replyDtoRepository = replyDtoRepository;
        this.messageSenderReceiverRepository = messageSenderReceiverRepository;
        messageWriteModelValidator = new MessageWriteModelValidator(userRepository);
        replyMessageWriteModelValidator = new ReplyMessageWriteModelValidator(messageDtoRepository,
                messageSenderReceiverRepository);
        setAvailableIdWithinExistingIds();
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
