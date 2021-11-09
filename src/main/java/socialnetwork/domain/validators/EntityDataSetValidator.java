package socialnetwork.domain.validators;


import socialnetwork.domain.models.Entity;
import socialnetwork.exceptions.CorruptedDataException;
import socialnetwork.repository.RepositoryInterface;

/**
 * Class used to validate data of a repository using a model validator
 * @param <ID> type identifier of the objects in the repository
 * @param <E> subclass of Entity with identifier of type ID
 */
public class EntityDataSetValidator<ID, E extends Entity<ID>> {

    private EntityValidatorInterface<ID, E> entityValidator;
    private RepositoryInterface<ID, E> repository;

    /**
     * Constructor that creates a new data validator on the given repository and using the validation rules of the
     * given validator
     * @param entityValidator validator for the models in the repository
     * @param repository repository containing the data that will be validated
     */
    public EntityDataSetValidator(EntityValidatorInterface<ID, E> entityValidator,
                                  RepositoryInterface<ID, E> repository) {
        this.entityValidator = entityValidator;
        this.repository = repository;
    }

    /**
     * Validates repository data
     * @param exceptionMessage message for the exception thrown if the data is not valid
     * @throws CorruptedDataException if repository data is not valid by the rules of the model validator
     */
    public void validateDataSet(String exceptionMessage){
        for(E entity : repository.getAll())
            if(!entityValidator.isValid(entity))
                throw new CorruptedDataException(exceptionMessage);
    }
}
