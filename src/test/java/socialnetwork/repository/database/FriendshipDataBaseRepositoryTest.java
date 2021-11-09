package socialnetwork.repository.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.Friendship;
import socialnetwork.repository.FriendshipRepositoryTestSetter;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @BeforeEach
    public void setUp(){
        var userTest = new UserDataBaseRepositoryTest();
        userTest.setUp();
        FriendshipDatabaseTableSetter.setUp(getTestData());
    }

    @AfterAll
    void restoreDataBase(){
        setUp();
    }
}