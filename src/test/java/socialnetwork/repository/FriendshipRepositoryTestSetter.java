package socialnetwork.repository;


import org.junit.jupiter.api.BeforeEach;
import socialnetwork.domain.models.Friendship;
import socialnetwork.utils.containers.UnorderedPair;

import java.util.Arrays;
import java.util.List;

public abstract class FriendshipRepositoryTestSetter
        extends RepositoryAbstractTest<UnorderedPair<Long, Long>, Friendship> {
    @Override
    public Friendship createValidEntity() {
        return new Friendship(1234L, 5678L);
    }


    @Override
    public UnorderedPair<Long, Long> createNotExistingId() {
        return new UnorderedPair<>(5555L, 6666L);
    }

    @Override
    public UnorderedPair<Long, Long> getExistingId() {
        return new UnorderedPair<>(2L, 1L);
    }

    @Override
    public List<Friendship> getTestData() {
        return Arrays.asList(
                new Friendship(1L, 2L),
                new Friendship(1L, 3L),
                new Friendship(2L, 3L)
        );
    }


}
