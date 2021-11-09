package socialnetwork.repository;


import socialnetwork.domain.models.Entity;

import java.util.List;
import java.util.Optional;

/**
 * CRUD repository interface
 * @param <ID> id type of E
 * @param <E> subclass of Entity
 */
public interface RepositoryInterface<ID, E extends Entity<ID>> {
    /**
     * Saves the given entity
     *
     * @param entity model that will be added to repository
     * @return empty Optional if the entity was saved, Optional containing the existing entity otherwise
     */
    Optional<E> save(E entity);

    /**
     * Returns a list of the saved models
     *
     * @return List containing all entities of the repository
     */
    List<E> getAll();

    /**
     * Finds an entity by the given id
     *
     * @param id identifier of the entity we want to find
     * @return Optional containing the found entity, an empty Optional if entity doesn't exist
     */
    Optional<E> findById(ID id);

    /**
     * Updates the entity with the given id and sets it to newValue
     *
     * @param newValue entity with the same id as the model we want to update and containing the new values for
     *                 firstName and lastName
     * @return Optional containing the old entity, empty Optional if newValue doesn't exist
     */
    Optional<E> update(E newValue);

    /**
     * Removes the entity with the given id
     *
     * @param id identifier of the entity we want to remove
     * @return Optional containing the old entity, empty Optional if entity doesn't exist
     */
    Optional<E> remove(ID id);
}
