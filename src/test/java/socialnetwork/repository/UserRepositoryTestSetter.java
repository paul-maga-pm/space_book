package socialnetwork.repository;


import org.junit.jupiter.api.BeforeEach;
import socialnetwork.domain.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class UserRepositoryTestSetter extends RepositoryAbstractTest<Long, User> {

    @Override
    public Long getExistingId(){
        return 1L;
    }

    @Override
    public Long createNotExistingId(){
        return 4L;
    }
    @Override
    public List<User> getTestData(){
        return new ArrayList<>(Arrays.asList(
                new User(1L, "Michael", "Michael"),
                new User(2L, "John", "John"),
                new User(3L, "Marcel", "Marcel"),
                new User(1234L, "John", "John"),
                new User(5678L, "John", "John")));
    }

    @Override
    public User createValidEntityThatIsNotInRepository() {
        return new User(10L, "Snow", "Snow");
    }

}
