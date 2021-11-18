package socialnetwork.repository;

import socialnetwork.domain.models.MessageDto;
import socialnetwork.repository.RepositoryAbstractTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public abstract class MessageDtoRepositoryTestSetter extends RepositoryAbstractTest<Long, MessageDto> {
    @Override
    public Long getExistingId(){
        return 1L;
    }

    @Override
    public Long createNotExistingId(){
        return 6L;
    }
    @Override
    public List<MessageDto> getTestData(){
        return Arrays.asList(
                new MessageDto(1L,"Hello world", LocalDateTime.of(2021, 10, 10, 10, 10, 10)),
                new MessageDto(2L,"Hello server", LocalDateTime.of(2021, 10, 10, 10, 10, 10)),
                new MessageDto(3L,"Hello John", LocalDateTime.of(2021, 10, 10, 10, 10, 10)),
                new MessageDto(4L,"Hello Bob", LocalDateTime.of(2021, 10, 10, 10, 10, 10)),
                new MessageDto(5L,"Hello Thomas", LocalDateTime.of(2021, 10, 10, 10, 10, 10))
        );
    }

    @Override
    public MessageDto createValidEntityThatIsNotInRepository() {
        return new MessageDto(6L, "Hello everyone", LocalDateTime.of(2021, 10, 10, 10, 10, 10));

    }

}
