package socialnetwork.repository.memory;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import socialnetwork.domain.models.Friendship;
import socialnetwork.repository.FriendshipRepositoryTestSetter;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

public class InMemoryFriendshipRepositoryTest extends FriendshipRepositoryTestSetter {
    InMemoryRepository<UnorderedPair<Long, Long>, Friendship> testRepository;
    @Override
    public RepositoryInterface<UnorderedPair<Long, Long>, Friendship> getRepository() {
        if(testRepository == null)
            testRepository = new InMemoryRepository<>();
        return testRepository;
    }

    @AfterEach
    void tearDown() {
        for (Friendship friendship : getRepository().getAll())
            getRepository().remove(friendship.getId());
    }

    @BeforeEach
    void setUp(){
        for(Friendship friendship : getTestData())
            getRepository().save(friendship);
    }
}
