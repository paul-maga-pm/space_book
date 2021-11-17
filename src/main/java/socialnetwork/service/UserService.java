package socialnetwork.service;


import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDate;
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

    /**
     *  Finds all the friends for a given user
     * @param id identifier of the user we want to find the friends for
     * @return map containing the friends of the given user (as keys) and the date since when they have been friends (as values)
     */
    public Map<Optional<User>, LocalDateTime> findAllFriendsForUserService(Long id){
        Map<Optional<User>, LocalDateTime> friendsForUser = new HashMap<>();
        List<Friendship> friendships = friendshipRepository.getAll();
        friendships.stream().filter(friendship -> friendship.hasUser(id))
                .forEach(friendship -> {
                    Long idOfFriend;
                    if(friendship.getId().first == id)
                        idOfFriend = friendship.getId().second;
                    else
                        idOfFriend = friendship.getId().first;
                    friendsForUser.put(userRepository.findById(idOfFriend), friendship.getDate());
                });
        return friendsForUser;
    }

    /**
     * Finds all friends of the user that became friends in the given month
     * @param idOfUser identifier of user we find the friends
     * @param month month when the users became friends
     * @return a Map with the key representing the friend of the user and the value the date when the users became
     * friends
     */
    public Map<Optional<User>, LocalDateTime> findAllFriendsForUserFromMonthService(Long idOfUser, int month){
        Map<Optional<User>, LocalDateTime> friendsOfUserMap = findAllFriendsForUserService(idOfUser);
        int year = LocalDateTime.now().getYear();
        Map<Optional<User>, LocalDateTime> friendsOfUserFromMonthMap = new HashMap<>();
        friendsOfUserMap.forEach((user, date) -> {
            if(date.getYear() == year && date.getMonth().getValue() == month)
                friendsOfUserFromMonthMap.put(user, date);
        });
        return friendsOfUserFromMonthMap;
    }
}
