package socialnetwork.repository.memory;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import socialnetwork.domain.models.User;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.UserRepositoryTestSetter;

class InMemoryUserRepositoryTest extends UserRepositoryTestSetter {
    InMemoryRepository<Long, User> testRepository;

    public RepositoryInterface<Long, User> getRepository(){
        if(testRepository == null)
            testRepository = new InMemoryRepository<>();
        return testRepository;
    }

    @AfterEach
    void tearDown(){
        for(User user : getRepository().getAll())
            getRepository().remove(user.getId());
    }

    @BeforeEach
    void setUp(){
        for(User user : getTestData())
            getRepository().save(user);
    }
}