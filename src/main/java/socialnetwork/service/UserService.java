package socialnetwork.service;


import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business layer for User model
 */


public class UserService {
    private RepositoryInterface<Long, User> userRepository;
    private RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepository;
    private EntityValidatorInterface<Long, User> userValidator;

    public UserService(RepositoryInterface<Long, User> userRepository,
                       EntityValidatorInterface<Long, User> userValidator){
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    /**
     * Adds a new user
     * @param id identifier of the user
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @return empty Optional if the user was added, Optional containing the existing user with the same id otherwise
     */
    public Optional<User> addUserService(Long id, String firstName, String lastName){
        User user = new User(id, firstName, lastName);
        userValidator.validate(user);
        return userRepository.save(user);
    }

    /**
     * Removes the user with the given id
     * @param id identifier of user
     * @return Optional with the user that was removed, empty Optional if the user didn't exist
     */
    public Optional<User> removeUserService(Long id){
        return userRepository.remove(id);
    }

    /**
     * Finds the user with the given id
     * @param id identifier of the user we want to find
     * @return empty Optional if the user with the given id doesn't exit, Optional with the existing user otherwise
     */
    public Optional<User> findUserByIdService(Long id){
        return userRepository.findById(id);
    }

    /**
     * Updates the user with the given id
     * @param id identifier of the user we want to update
     * @param newFirstName new value for firstName field
     * @param newLastName new value for lastName field
     * @throws InvalidEntityException if the id, newFirstName, newLastName are not valid
     */
    public Optional<User> updateUserService(Long id, String newFirstName, String newLastName){
        User newValue = new User(id, newFirstName, newLastName);
        userValidator.validate(newValue);
        return userRepository.update(newValue);
    }
}
