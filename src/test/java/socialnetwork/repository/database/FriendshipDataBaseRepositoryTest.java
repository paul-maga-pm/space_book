package socialnetwork.repository.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.repository.FriendshipRepositoryTestSetter;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

class FriendshipDataBaseRepositoryTest extends FriendshipRepositoryTestSetter {
    FriendshipDatabaseRepository testRepository;
    private String url = ApplicationContext.getProperty("network.database.url");
    private String user = ApplicationContext.getProperty("network.database.user");
    private String password = ApplicationContext.getProperty("network.database.password");

    @Override
    public RepositoryInterface<UnorderedPair<Long, Long>, Friendship> getRepository() {
        if(testRepository == null)
            testRepository = new FriendshipDatabaseRepository(url, user, password);
        return testRepository;
    }

    public static void setUpUsersTable(){
        UserDatabaseTableSetter.setUp(Arrays.asList(
                new User(1L, "Michael", "Michael"),
                new User(2L, "John", "John"),
                new User(3L, "Marcel", "Marcel"),
                new User(1234L, "John", "John"),
                new User(5678L, "John", "John")));
    }

    @BeforeAll
    public static void setUpDatabase(){
        DatabaseCleaner.clearDatabase();
        setUpUsersTable();
    }

    @BeforeEach
    public void setUp(){
       FriendshipDatabaseTableSetter.setUp(getTestData());
    }
}