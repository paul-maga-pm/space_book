package socialnetwork.repository.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.MessageDto;
import socialnetwork.domain.models.MessageSenderReceiverDto;
import socialnetwork.domain.models.MessageSenderReceiverDtoId;
import socialnetwork.domain.models.User;
import socialnetwork.repository.MessageSenderReceiverDtoRepositoryTestSetter;
import socialnetwork.repository.RepositoryInterface;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class MessageSenderReceiverDatabaseRepositoryTest
extends MessageSenderReceiverDtoRepositoryTestSetter {
    String url = ApplicationContext.getProperty("network.database.url");
    String user = ApplicationContext.getProperty("network.database.user");
    String password = ApplicationContext.getProperty("network.database.password");
    MessageSenderReceiverDtoDatabaseRepository testRepository = null;

    @Override
    public RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> getRepository() {
        if(testRepository == null)
            testRepository = new MessageSenderReceiverDtoDatabaseRepository(url, user, password);
        return testRepository;
    }

    @BeforeAll
    static void setUpDatabase(){
        DatabaseCleaner.clearDatabase();

        setUpUsersTable();
        setUpMessagesTable();
    }

    static void setUpUsersTable(){
        List<User> users = Arrays.asList(
                new User(1L, "John", "John"),
                new User(2L, "Bob", "Bob"),
                new User(3L, "Dave", "Dave"),
                new User(4L, "Jordan", "Jordan")
        );
        UserDatabaseTableSetter.setUp(users);
    }

    static void setUpMessagesTable(){
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
        MessagesSenderReceiverDatabaseTableSetter.setUp(getTestData());
    }
}
