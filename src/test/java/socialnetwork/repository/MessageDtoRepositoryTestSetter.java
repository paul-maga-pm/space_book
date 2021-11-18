package socialnetwork.repository;

import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.MessageDto;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class MessageDtoRepositoryTestSetter extends RepositoryAbstractTest<Long, MessageDto> {
    private static List<MessageDto> testDataList = Arrays.asList(
            new MessageDto(1L,"Hello world", LocalDateTime.of(2021, 10, 10, 10, 10, 10)),
            new MessageDto(2L,"Hello server", LocalDateTime.of(2021, 10, 10, 10, 10, 10)),
            new MessageDto(3L,"Hello John", LocalDateTime.of(2021, 10, 10, 10, 10, 10)),
            new MessageDto(4L,"Hello Bob", LocalDateTime.of(2021, 10, 10, 10, 10, 10)),
            new MessageDto(5L,"Hello Thomas", LocalDateTime.of(2021, 10, 10, 10, 10, 10))
    );
    private static final HashMap<Long, MessageDto> testDataMap = new HashMap<>(){{
        for(var data : testDataList)
            put(data.getId(), data);
    }};

    private static final MessageDto validEntityThatIsNotInRepository =
            new MessageDto(6L, "Hello everyone", LocalDateTime.of(2021, 10, 10, 10, 10, 10));
    private static final MessageDto oldValueForUpdateTest =
            new MessageDto(1L,"Hello world", LocalDateTime.of(2021, 10, 10, 10, 10, 10));

    private static final MessageDto newValueForUpdateTest =
            new MessageDto(1L,"Bye world", LocalDateTime.of(2000, 1, 1, 11, 10, 10));
    ;

    @Override
    public Long getExistingId(){
        return 1L;
    }

    @Override
    public Long getNotExistingId(){
        return 6L;
    }
    @Override
    public List<MessageDto> getTestData(){
        return testDataList;
    }

    @Override
    public MessageDto getValidEntityThatIsNotInRepository() {
        return validEntityThatIsNotInRepository;
    }

    @Override
    public MessageDto getOldValueOfEntityForUpdateTest() {
        return oldValueForUpdateTest;
    }

    @Override
    public MessageDto getNewValueForEntityForUpdateTest() {
        return newValueForUpdateTest;
    }

    @Override
    public MessageDto getEntityWithId(Long id) {
        return testDataMap.get(id);
    }
}
