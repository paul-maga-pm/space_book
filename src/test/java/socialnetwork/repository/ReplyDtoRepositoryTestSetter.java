package socialnetwork.repository;

import socialnetwork.domain.models.ReplyDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class ReplyDtoRepositoryTestSetter
        extends RepositoryAbstractTest<Long, ReplyDto>  {
    private static final List<ReplyDto> testDataList = Arrays.asList(
            new ReplyDto(1L, 2L),
            new ReplyDto(3L, 2L),
            new ReplyDto(4L, 5L)
    );

    private static final HashMap<Long, ReplyDto> testDataMap = new HashMap<>(){{
        for(var data : testDataList)
            put(data.getId(), data);
    }};

    private static final ReplyDto validEntityThatIsNotInRepository = new ReplyDto(6L, 4L);
    private static final ReplyDto oldValueForUpdateTest =
            new ReplyDto(1L, 2L);
    private static final ReplyDto newValueForUpdateTest =
            new ReplyDto(1L, 2L);

    @Override
    public ReplyDto getValidEntityThatIsNotInRepository() {
        return validEntityThatIsNotInRepository;
    }

    @Override
    public Long getNotExistingId() {
        return 10L;
    }

    @Override
    public Long getExistingId() {
        return 1L;
    }

    @Override
    public List<ReplyDto> getTestData() {
        return testDataList;
    }

    @Override
    public ReplyDto getOldValueOfEntityForUpdateTest() {
        return oldValueForUpdateTest;
    }

    @Override
    public ReplyDto getNewValueForEntityForUpdateTest() {
        return newValueForUpdateTest;
    }

    @Override
    public ReplyDto getEntityWithId(Long id) {
        return testDataMap.get(id);
    }
}
