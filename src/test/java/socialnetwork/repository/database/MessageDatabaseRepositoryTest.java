package socialnetwork.repository.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.MessageDto;
import socialnetwork.repository.MessageDtoRepositoryTestSetter;
import socialnetwork.repository.RepositoryInterface;

import java.sql.*;

public class MessageDatabaseRepositoryTest extends MessageDtoRepositoryTestSetter {
    String url = ApplicationContext.getProperty("network.database.url");
    String user = ApplicationContext.getProperty("network.database.user");
    String password = ApplicationContext.getProperty("network.database.password");
    MessageDtoDatabaseRepository testRepository = null;

    @Override
    public RepositoryInterface<Long, MessageDto> getRepository() {
        if(testRepository == null)
            testRepository = new MessageDtoDatabaseRepository(url, user, password);
        return testRepository;
    }


    @BeforeAll
    static void setUpDataBase(){
        DatabaseCleaner.clearDatabase();
        MessageDatabaseTableSetter.tearDown();
    }

    @BeforeEach
    void setUp(){
        MessageDatabaseTableSetter.setUp(getTestData());
    }
}
