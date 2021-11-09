package socialnetwork.domain.validators;


import socialnetwork.domain.models.Entity;
import socialnetwork.exceptions.InvalidEntityException;

/**
 * Interface that defines a validator for a model
 * @param <ID> type of the identifier of the model
 *  @param <E> type of the model that is a subclass of Entity and has the identifier of the ID
 */
public interface EntityValidatorInterface<ID, E extends Entity<ID>> {
    /**
     * Validates the given entity
     * @param entity Entity that will be validated
     * @throws IllegalArgumentException if entity is null
     * @throws InvalidEntityException if the entity is not valid
     * */
    void validate(E entity);

    /**
     * Validates the given entity
     * @param entity Entity that will be validated
     * @return true if entity is valid, false otherwise
     * @throws IllegalArgumentException if entity is null
     * */
    boolean isValid(E entity);
}
