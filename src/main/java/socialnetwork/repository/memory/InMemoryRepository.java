package socialnetwork.repository.memory;


import socialnetwork.domain.entities.Entity;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In memory repository implementation for a model E
 * @param <ID> type of the identifier of the model
 * @param <E> subclass of Entity that has the identifier of tye ID
 */
public class InMemoryRepository <ID, E extends Entity<ID>>
        implements Repository<ID, E> {
    private Map<ID, E> entitiesMap = new HashMap<ID, E>();

    @Override
    public Optional<E> save(E entity) {
        if(entitiesMap.containsKey(entity.getId())) {
            E existingValue =  entitiesMap.get(entity.getId());
            return Optional.of(existingValue);
        }
        entitiesMap.put(entity.getId(), entity);
        return Optional.empty();
    }

    @Override
    public List<E> getAll() {
        return entitiesMap.values().stream().toList();
    }

    @Override
    public Optional<E> findById(ID id) {
        if(entitiesMap.containsKey(id))
            return Optional.of(entitiesMap.get(id));
        return Optional.empty();
    }

    @Override
    public Optional<E> update(E newValue) {
        if(entitiesMap.containsKey(newValue.getId())){
            E oldValue = entitiesMap.put(newValue.getId(), newValue);
            return Optional.of(oldValue);
        }
        return Optional.empty();
    }

    @Override
    public Optional<E> remove(ID id) {
        if(entitiesMap.containsKey(id)){
            E oldValue = entitiesMap.remove(id);
            return Optional.of(oldValue);
        }
        return Optional.empty();
    }

    /**
     * Removes all local data
     */
    public void removeAllLocalData() {
        entitiesMap.clear();
    }
}
