package socialnetwork.repository.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.MessageDto;
import socialnetwork.domain.models.ReplyDto;
import socialnetwork.repository.ReplyDtoRepositoryTestSetter;
import socialnetwork.repository.RepositoryInterface;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ReplyDtoDatabaseRepositoryTest
        extends ReplyDtoRepositoryTestSetter {
    static String url = ApplicationContext.getProperty("network.database.url");
    static String user = ApplicationContext.getProperty("network.database.user");
    static String password = ApplicationContext.getProperty("network.database.password");
    ReplyDtoDatabaseRepository testRepository = null;

    @Override
    public RepositoryInterface<Long, ReplyDto> getRepository() {
        if(testRepository == null)
            testRepository = new ReplyDtoDatabaseRepository(url, user, password);
        return testRepository;
    }

    @BeforeAll
    static void setUpDatabase(){
        DatabaseCleaner.clearDatabase();
        setUpMessageDatabaseTable();
    }

    public static void setUpMessageDatabaseTable() {
        List<MessageDto> messages = Arrays.asList(
                new MessageDto(1L, "Hello Bob", LocalDateTime.now()),
                new MessageDto(2L, "Hello Bob", LocalDateTime.now()),
                new MessageDto(3L, "Hello John", LocalDateTime.now()),
                new MessageDto(4L, "Hello Bob", LocalDateTime.now()),
                new MessageDto(5L, "Hello Dave", LocalDateTime.now()),
                new MessageDto(6L, "Hello Bob", LocalDateTime.now())
        );
        MessageDatabaseTableSetter.setUp(messages);
    }

    @BeforeEach
    void setUp(){
        ReplyDatabaseTableSetter.setUp(getTestData());
    }
}
