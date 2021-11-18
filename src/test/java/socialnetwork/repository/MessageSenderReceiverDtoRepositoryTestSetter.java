package socialnetwork.repository;

import socialnetwork.domain.models.MessageSenderReceiverDto;
import socialnetwork.domain.models.MessageSenderReceiverDtoId;

import java.util.Arrays;
import java.util.List;

public abstract class MessageSenderReceiverDtoRepositoryTestSetter
extends RepositoryAbstractTest<MessageSenderReceiverDtoId, MessageSenderReceiverDto> {
    @Override
    public MessageSenderReceiverDto createValidEntityThatIsNotInRepository() {
        return new MessageSenderReceiverDto(6L, 1L, 2L);
    }

    @Override
    public MessageSenderReceiverDtoId createNotExistingId() {
        return new MessageSenderReceiverDtoId(7L, 2L, 3L);
    }

    @Override
    public MessageSenderReceiverDtoId getExistingId() {
        return new MessageSenderReceiverDtoId(1L, 1L, 2L);
    }

    @Override
    public List<MessageSenderReceiverDto> getTestData() {
        return Arrays.asList(
                new MessageSenderReceiverDto(1L, 1L, 2L),
                new MessageSenderReceiverDto(2L, 1L, 2L),
                new MessageSenderReceiverDto(3L, 2L, 1L),
                new MessageSenderReceiverDto(4L, 3L, 2L),
                new MessageSenderReceiverDto(5L, 2L, 3L)
        );
    }
}
