package socialnetwork.repository;


import socialnetwork.domain.models.Friendship;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class FriendshipRepositoryTestSetter
        extends RepositoryAbstractTest<UnorderedPair<Long, Long>, Friendship> {
    private static final Friendship validFriendshipThatIsNotInRepository =
            new Friendship(1234L, 5678L, LocalDateTime.of(2021, 1, 2, 2, 30));

    private static final List<Friendship> testDataList = Arrays.asList(
            new Friendship(1L, 2L, LocalDateTime.of(2021, 10, 20, 10, 30)),
            new Friendship(1L, 3L, LocalDateTime.of(2021, 10, 20, 10, 30)),
            new Friendship(2L, 3L, LocalDateTime.of(2021, 10, 20, 10, 30))
    );
    private static final HashMap<UnorderedPair<Long, Long>, Friendship> testDataMap = new HashMap<>(){{
        for(var data : testDataList)
            put(data.getId(), data);
    }};

    private static final UnorderedPair<Long, Long> exitingId = new UnorderedPair<>(2L, 1L);
    private static final UnorderedPair<Long, Long> notExistingId = new UnorderedPair<>(5555L, 6666L);
    private static final Friendship updateOldValue = new Friendship(1L, 2L, LocalDateTime.of(2021, 10, 20, 10, 30));
    private static final Friendship updateNewValue = new Friendship(1L, 2L, LocalDateTime.of(2019, 1, 1, 1, 30));

    @Override
    public Friendship getValidEntityThatIsNotInRepository() {
        return validFriendshipThatIsNotInRepository;}


    @Override
    public UnorderedPair<Long, Long> getNotExistingId() {
        return notExistingId;
    }

    @Override
    public UnorderedPair<Long, Long> getExistingId() {
        return exitingId;
    }

    @Override
    public List<Friendship> getTestData() {
        return testDataList;
    }

    @Override
    public Friendship getOldValueOfEntityForUpdateTest(){
        return updateOldValue;
    }

    @Override
    public Friendship getNewValueForEntityForUpdateTest(){
        return updateNewValue;
    }

    @Override
    public Friendship getEntityWithId(UnorderedPair<Long, Long> id) {
        return testDataMap.get(id);
    }
}
