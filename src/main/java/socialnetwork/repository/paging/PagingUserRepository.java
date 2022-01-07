package socialnetwork.repository.paging;

import socialnetwork.domain.entities.User;
import socialnetwork.repository.Repository;

public interface PagingUserRepository extends Repository<Long, User> {
    PageInterface<User> findAllUsersByName(PageableInterface pageable, String name);

    int countUsersThatHaveInTheirNameTheString(String str);
}
