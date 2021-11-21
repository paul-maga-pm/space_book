package socialnetwork.domain.validators;

import socialnetwork.domain.models.MessageDto;
import socialnetwork.domain.models.MessageSenderReceiverDto;
import socialnetwork.domain.models.MessageSenderReceiverDtoId;
import socialnetwork.domain.models.ReplyMessageWriteModel;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;

public class ReplyMessageWriteModelValidator {
    private RepositoryInterface<Long, MessageDto> messageDtoRepository;
    private RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto>
        messageSenderReceiverDtoRepository;

    public ReplyMessageWriteModelValidator(RepositoryInterface<Long, MessageDto> messageDtoRepository,
                                           RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto>
                                                   messageSenderReceiverDtoRepository){
        this.messageDtoRepository = messageDtoRepository;
        this.messageSenderReceiverDtoRepository = messageSenderReceiverDtoRepository;
    }

    public void validate(ReplyMessageWriteModel replyMessageWriteModel){
        validateThatMessageThatRepliesToExist(replyMessageWriteModel);
        validateThatSenderOfTheReplyIsAReceiverOfTheMessage(replyMessageWriteModel);
    }

    private void validateThatMessageThatRepliesToExist(ReplyMessageWriteModel replyMessage) {
        Long idOfMessage = replyMessage.getIdOfMessageThatRepliesTo();
        boolean messageExists = messageDtoRepository.findById(idOfMessage).isPresent();
        if(!messageExists)
            throw new InvalidEntityException("Message that is replied to must exist");
    }

    private void validateThatSenderOfTheReplyIsAReceiverOfTheMessage(ReplyMessageWriteModel replyMessage) {
        Long messageRepliedToId = replyMessage.getIdOfMessageThatRepliesTo();
        Long receiverId = replyMessage.getIdOfSender();
        boolean isSenderAReceiver = messageSenderReceiverDtoRepository
                .getAll()
                .stream()
                .filter(dto -> dto.getId().getMessageId().equals(messageRepliedToId))
                .anyMatch(dto -> dto.getId().getReceiverId().equals(receiverId));
        if(!isSenderAReceiver)
            throw new InvalidEntityException("Sender must be part of the receivers " +
                    "of the message he wants to reply to");
    }
}
