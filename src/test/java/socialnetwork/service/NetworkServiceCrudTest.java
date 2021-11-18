package socialnetwork.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.csv.FriendshipCSVFileRepository;
import socialnetwork.repository.csv.UserCSVFileRepository;
import socialnetwork.repository.database.*;
import socialnetwork.utils.containers.UndirectedGraph;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.*;

public class NetworkServiceCrudTest {
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

    NetworkService testService = new NetworkService(friendshipTestRepository,
            userTestRepository,
            new FriendshipValidator(userTestRepository));

    @BeforeEach
    void setUp(){
        DatabaseCleaner.clearDatabase();
        UserDatabaseTableSetter.setUp(localUsers.getAll());
        FriendshipDatabaseTableSetter.setUp(localFriendships.getAll());
    }

    @Test
    void addExistingFriendship(){
        Assertions.assertTrue(testService.addFriendshipService(2L, 1L, LocalDateTime.now()).isPresent());
    }

    @Test
    void addNewFriendship(){
        Assertions.assertTrue(testService.addFriendshipService(12L, 11L, LocalDateTime.now()).isEmpty());
    }

    @Test
    void addWithNonExistingUsers(){
        Assertions.assertThrows(InvalidEntityException.class,
                ()->testService.addFriendshipService(1000L, 2000L, LocalDateTime.now()));
    }

    @Test
    void removeWithNonExistingFriendship(){
        Assertions.assertTrue(testService.removeFriendshipService(12L, 11L).isEmpty());
    }

    @Test
    void removeWithExistingFriendship(){
        Assertions.assertTrue(testService.removeFriendshipService(1L, 2L).isPresent());
    }

    @Test
    void getUsersWithAllFriends(){
        UndirectedGraph<User> graph = new UndirectedGraph<>(localUsers.getAll());

        for(Friendship friendship : localFriendships.getAll()) {
            User user1 = localUsers.findById(friendship.getId().first).get();
            User user2 = localUsers.findById(friendship.getId().second).get();
            graph.addEdge(user1, user2);
        }

        for(User user : testService.getAllUsersAndTheirFriendsService()){
            Set<User> expectedFriends = graph.getNeighboursOf(user);
            Assertions.assertEquals(expectedFriends.size(), user.getFriendsList().size());
            Assertions.assertTrue(expectedFriends.containsAll(user.getFriendsList()));
        }
    }
}
