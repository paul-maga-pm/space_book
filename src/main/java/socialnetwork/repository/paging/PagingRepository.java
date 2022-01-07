package socialnetwork.repository.paging;

import socialnetwork.domain.entities.Entity;
import socialnetwork.repository.Repository;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface PagingRepository<Id, E extends Entity<Id>> extends Repository<Id, E> {
}
