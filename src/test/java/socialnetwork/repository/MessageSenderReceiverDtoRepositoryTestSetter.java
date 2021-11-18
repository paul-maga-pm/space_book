package socialnetwork.repository;

import socialnetwork.domain.models.MessageDto;
import socialnetwork.domain.models.MessageSenderReceiverDto;
import socialnetwork.domain.models.MessageSenderReceiverDtoId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class MessageSenderReceiverDtoRepositoryTestSetter
extends RepositoryAbstractTest<MessageSenderReceiverDtoId, MessageSenderReceiverDto> {
    public static final List<MessageSenderReceiverDto> testDataList = Arrays.asList(
            new MessageSenderReceiverDto(1L, 1L, 2L),
            new MessageSenderReceiverDto(2L, 1L, 2L),
            new MessageSenderReceiverDto(3L, 2L, 1L),
            new MessageSenderReceiverDto(4L, 3L, 2L),
            new MessageSenderReceiverDto(5L, 2L, 3L)
    );
    private static final HashMap<MessageSenderReceiverDtoId, MessageSenderReceiverDto> testDataMap = new HashMap<>(){{
        for(var data : testDataList)
            put(data.getId(), data);
    }};


    public static final MessageSenderReceiverDto validEntityThatIsNotInRepository =
            new MessageSenderReceiverDto(6L, 1L, 2L);

    public static final MessageSenderReceiverDto oldValueForUpdateTest =
            new MessageSenderReceiverDto(1L, 1L, 2L);

    public static final MessageSenderReceiverDto newValueForUpdateTest =
            new MessageSenderReceiverDto(1L, 1L, 2L);

    public static final MessageSenderReceiverDtoId notExistingId = new MessageSenderReceiverDtoId(7L, 2L, 3L);;
    public static final MessageSenderReceiverDtoId existingId = new MessageSenderReceiverDtoId(1L, 1L, 2L);;

    @Override
    public MessageSenderReceiverDto getValidEntityThatIsNotInRepository() {
        return validEntityThatIsNotInRepository;
    }

    @Override
    public MessageSenderReceiverDtoId getNotExistingId() {
        return notExistingId;
    }

    @Override
    public MessageSenderReceiverDtoId getExistingId() {
        return existingId;
    }

    @Override
    public List<MessageSenderReceiverDto> getTestData() {
        return testDataList;
    }

    @Override
    public MessageSenderReceiverDto getOldValueOfEntityForUpdateTest() {
        return oldValueForUpdateTest;
    }

    @Override
    public MessageSenderReceiverDto getNewValueForEntityForUpdateTest() {
        return newValueForUpdateTest;
    }

    @Override
    public MessageSenderReceiverDto getEntityWithId(MessageSenderReceiverDtoId messageSenderReceiverDtoId) {
        return testDataMap.get(messageSenderReceiverDtoId);
    }
}
