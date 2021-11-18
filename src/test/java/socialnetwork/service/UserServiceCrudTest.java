package socialnetwork.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.csv.FriendshipCSVFileRepository;
import socialnetwork.repository.csv.UserCSVFileRepository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.memory.InMemoryRepository;
import socialnetwork.utils.containers.UnorderedPair;

public class UserServiceCrudTest {
    private String url = ApplicationContext.getProperty("network.database.url");
    private String user = ApplicationContext.getProperty("network.database.user");
    private String password = ApplicationContext.getProperty("network.database.password");
    RepositoryInterface<Long, User> userTestRepository = new UserDatabaseRepository(url, user, password);;
    RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipTestRepository
            = new FriendshipDatabaseRepository(url, user, password);;
    UserCSVFileRepository localUsers =
            new UserCSVFileRepository(ApplicationContext.getProperty("service.users.crud"));
    FriendshipCSVFileRepository localFriendships =
            new FriendshipCSVFileRepository(ApplicationContext.getProperty("service.friendships.crud"));
    UserService testService = new UserService(userTestRepository, friendshipTestRepository, new UserValidator());

    @BeforeEach
    void setUp(){
        DatabaseCleaner.clearDatabase();
        UserDatabaseTableSetter.setUp(localUsers.getAll());
        FriendshipDatabaseTableSetter.setUp(localFriendships.getAll());
    }

    @Test
    void addWithInvalidUser(){
        Assertions.assertThrows(InvalidEntityException.class,
                ()->testService.addUserService(-1000L, "", ""));
    }

    @Test
    void addWithValidUser(){
        Assertions.assertTrue(testService.addUserService(1000L, "John", "Snow").isEmpty());
    }

    @Test
    void addWithExistingUser(){
        Assertions.assertTrue(testService.addUserService(1L, "John", "Snow").isPresent());
    }

    @Test
    void removeNonExitingUser(){
        Assertions.assertTrue(testService.removeUserService(1000L).isEmpty());
    }

    @Test
    void removeExistingUserWithNoFriends(){
        int oldNumberOfFriendships = friendshipTestRepository.getAll().size();
        Assertions.assertTrue(testService.removeUserService(10L).isPresent());
        Assertions.assertEquals(oldNumberOfFriendships, friendshipTestRepository.getAll().size());
    }

    @Test
    void removeExistingUserWithFriends(){
        int oldNumberOfFriends = friendshipTestRepository.getAll().size();
        Assertions.assertTrue(testService.removeUserService(1L).isPresent());
        Assertions.assertEquals(oldNumberOfFriends - 3, friendshipTestRepository.getAll().size());
    }
}
