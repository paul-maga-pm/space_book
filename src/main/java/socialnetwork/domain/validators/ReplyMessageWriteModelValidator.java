package socialnetwork.domain.validators;

import socialnetwork.domain.models.*;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;

public class ReplyMessageWriteModelValidator {
    private RepositoryInterface<Long, MessageDto> messageDtoRepository;
    private RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto>
        messageSenderReceiverDtoRepository;
    private RepositoryInterface<Long, ReplyDto> replyDtoRepository;

    public ReplyMessageWriteModelValidator(RepositoryInterface<Long, MessageDto> messageDtoRepository,
                                           RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto>
                                                   messageSenderReceiverDtoRepository,
                                           RepositoryInterface<Long, ReplyDto> replyDtoRepository){
        this.messageDtoRepository = messageDtoRepository;
        this.messageSenderReceiverDtoRepository = messageSenderReceiverDtoRepository;
        this.replyDtoRepository = replyDtoRepository;
    }

    public void validate(ReplyMessageWriteModel replyMessageWriteModel){
        validateThatReplyDoesntReplyToAnotherReply(replyMessageWriteModel);
        validateThatMessageThatRepliesToExist(replyMessageWriteModel);
        validateThatSenderOfTheReplyIsAReceiverOfTheMessage(replyMessageWriteModel);
    }

    private void validateThatReplyDoesntReplyToAnotherReply(ReplyMessageWriteModel replyMessageWriteModel) {
        if(replyDtoRepository.findById(replyMessageWriteModel.getIdOfMessageThatRepliesTo()).isPresent())
            throw new InvalidEntityException("You can't reply to another reply");
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
