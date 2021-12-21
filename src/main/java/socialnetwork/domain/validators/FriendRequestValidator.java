package socialnetwork.domain.validators;

import socialnetwork.domain.entities.FriendRequest;
import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.containers.UnorderedPair;

/**
 * Validator for FriendRequest model
 */
public class FriendRequestValidator implements EntityValidator<UnorderedPair<Long, Long>, FriendRequest> {

    private Repository<Long, User> userRepository;

    /**
     * Constructor that creates a new validator that accesses the given user repository for friendRequest validation
     * @param userRepository repository containing the users
     * @throws IllegalArgumentException if userRepository is null
     */
    public FriendRequestValidator(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validates the existence of the users in the repository (given in the constructor) of the given friendRequest
     * @param friendRequest Entity that will be validated
     * @throws EntityNotFoundValidationException if the users in the given friendRequest don't exist in the user repository
     */
    @Override
    public void validate(FriendRequest friendRequest) {
        String errorMessage = "";
        Long idOfFirstUser = friendRequest.getId().first;
        Long idOfSecondUser = friendRequest.getId().second;
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
     * Checks if the user exists in the repository
     * @param id identifier of user
     * @return true if the user exists in the repository, false otherwise
     */
    private boolean checkIfUserExists(Long id){
        return userRepository.findById(id).isPresent();
    }
}
