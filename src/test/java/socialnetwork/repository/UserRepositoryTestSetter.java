package socialnetwork.repository;


import socialnetwork.domain.models.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class UserRepositoryTestSetter extends RepositoryAbstractTest<Long, User> {
    private static final List<User> testDataList = Arrays.asList(
            new User(1L, "Michael", "Michael"),
            new User(2L, "John", "John"),
            new User(3L, "Marcel", "Marcel"),
            new User(1234L, "John", "John"),
            new User(5678L, "John", "John"));

    private static final HashMap<Long, User> testDataMap = new HashMap<>(){{
        for(var data : testDataList)
            put(data.getId(), data);
    }};

    private static final User validUserThatIsNotInRepository = new User(10L, "Snow", "Snow");
    private static final User updateOldValue = new User(1L, "Michael", "Michael");
    private static final User updateNewValue = new User(1L, "Snow", "Snow");

    @Override
    public Long getExistingId(){
        return 1L;
    }

    @Override
    public Long getNotExistingId(){
        return 4L;
    }
    @Override
    public List<User> getTestData(){
        return testDataList;
    }

    @Override
    public User getValidEntityThatIsNotInRepository() {
        return validUserThatIsNotInRepository;
    }

    @Override
    public User getOldValueOfEntityForUpdateTest() {
        return updateOldValue;
    }

    @Override
    public User getNewValueForEntityForUpdateTest() {
        return updateNewValue;
    }

    @Override
    public User getEntityWithId(Long id) {
        return testDataMap.get(id);
    }
}
