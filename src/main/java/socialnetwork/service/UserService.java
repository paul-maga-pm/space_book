package socialnetwork.service;


import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.util.List;
import java.util.Optional;

/**
 * Business layer for User model
 */


public class UserService {
    private RepositoryInterface<Long, User> userRepository;
    private RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepository;
    private EntityValidatorInterface<Long, User> userValidator;

    /**
     * Creates a new service that accesses the given repositories and validates the users by the given validator's rules
     * @param userRepository repository containing the users
     * @param friendshipRepository repository containing the friendships
     * @param userValidator validator for User model
     */
    public UserService(RepositoryInterface<Long, User> userRepository,
                       RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepository, EntityValidatorInterface<Long, User> userValidator){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
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
     * Removes the user with the given id and his friendships with other users
     * @param id identifier of user
     * @return Optional with the user that was removed, empty Optional if the user didn't exist
     * @throws IllegalArgumentException if id was null
     */
    public Optional<User> removeUserService(Long id){
        if(userRepository.findById(id).isEmpty())
            return Optional.empty();

        List<Friendship> friendships = friendshipRepository.getAll();

        friendships.forEach((friendship) -> {
            if(friendship.hasUser(id))
                friendshipRepository.remove(friendship.getId());
        });

        return userRepository.remove(id);
    }

}
