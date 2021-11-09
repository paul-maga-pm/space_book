package socialnetwork.domain.validators;


import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

/**
 * Validator for Friendship model
 */
public class FriendshipValidator
        implements EntityValidatorInterface<UnorderedPair<Long, Long>, Friendship> {

    private RepositoryInterface<Long, User> userRepository;

    /**
     * Constructor that creates a new validator that accesses the given user repository for friendship validation
     * @param userRepository repository containing the users
     * @throws IllegalArgumentException if userRepository is null
     */
    public FriendshipValidator(RepositoryInterface<Long, User> userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validates the existence of the users in the repository (given in the constructor) of the given friendship
     * @param friendship Entity that will be validated
     * @throws EntityNotFoundValidationException if the users in the given friendship don't exist in the user repository
     */
    @Override
    public void validate(Friendship friendship) {
        String errorMessage = "";
        Long idOfFirstUser = friendship.getId().first;
        Long idOfSecondUser = friendship.getId().second;
        if(idOfFirstUser.equals(idOfSecondUser))
            throw new InvalidEntityException("Id's must be different");
        if(!checkIfUserExists(idOfFirstUser))
            errorMessage += "User with id " + idOfFirstUser + " doesn't exist\n";
        if(!checkIfUserExists(idOfSecondUser))
            errorMessage += "User with id " + idOfSecondUser + " doesn't exist";
        if(errorMessage.length() > 0)
            throw new EntityNotFoundValidationException(errorMessage);
    }

    /**
     * Validates the existence of the users in the repository (given in the constructor) of the given friendship
     * @param friendship Entity that will be validated
     * @return true if the users of the friendship exist in the repository, false otherwise
     */
    @Override
    public boolean isValid(Friendship friendship) {
        return checkIfUserExists(friendship.getId().first) &&
                checkIfUserExists((friendship.getId().second));
    }

    /**
     * Checks if the user exists in the repository
     * @param id identifier of user
     * @return true if the user exists in the repository, false otherwise
     */
    private boolean checkIfUserExists(Long id){
        return userRepository.findById(id).isPresent();
    }
}
