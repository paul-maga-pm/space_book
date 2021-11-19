package socialnetwork.service;

import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.repository.RepositoryInterface;

public class ConversationService {
    private RepositoryInterface<Long, MessageDto> messageDtoRepository;
    private RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverDtoRepository;
    private RepositoryInterface<Long, ReplyDto> replyDtoRepository;
    private EntityValidatorInterface<Long, Message> messageValidator;

    private static long idAvailable = 0;

    public ConversationService(RepositoryInterface<Long, MessageDto> messageDtoRepository,
                               RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverDtoRepository,
                               RepositoryInterface<Long, ReplyDto> replyDtoRepository,
                               EntityValidatorInterface<Long, Message> messageValidator) {
        this.messageDtoRepository = messageDtoRepository;
        this.messageSenderReceiverDtoRepository = messageSenderReceiverDtoRepository;
        this.replyDtoRepository = replyDtoRepository;
        this.messageValidator = messageValidator;

        getIdAvailable();
    }

    private void getIdAvailable(){
        idAvailable = messageDtoRepository.getAll().stream().map(messageDto -> messageDto.getId()).max(Long::compare).get() + 1;
    }
}
