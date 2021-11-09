package socialnetwork.domain.validators;


import org.junit.jupiter.api.Test;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.memory.InMemoryRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FriendshipValidatorTest {
    RepositoryInterface<Long, User> createUserRepository() {
        return new InMemoryRepository<>();

    }

    void setUpUserRepository(RepositoryInterface<Long, User> userRepository){
        List<User> userTestData = new ArrayList<>(Arrays.asList(
                new User(1L, "Michael", "Michael"),
                new User(2L, "John", "John"),
                new User(3L, "Marcel", "Marcel")));
        for(User user : userTestData)
            userRepository.save(user);
    }

    FriendshipValidator createStrategy(){
        var testRepo = createUserRepository();
        setUpUserRepository(testRepo);
        return new FriendshipValidator(testRepo);
    }

    @Test
    void usersDontExist(){
        var strategy = createStrategy();
        assertThrows(EntityNotFoundValidationException.class,
                () -> strategy.validate(new Friendship(1000L, 2000L)));
    }

    @Test
    void oneUserDoesntExist(){
        var strategy = createStrategy();
        assertThrows(EntityNotFoundValidationException.class,
                () -> strategy.validate(new Friendship(1L, 2000L)));
        assertThrows(EntityNotFoundValidationException.class,
                () -> strategy.validate(new Friendship(1000L, 2L)));
    }

    @Test
    void usersExist(){
        var strategy = createStrategy();
        strategy.validate(new Friendship(1L, 2L));
    }
}