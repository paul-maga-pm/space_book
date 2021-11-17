package socialnetwork.controllers;


import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.service.NetworkService;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller between ui and business layer
 */
public class NetworkController {
    private UserService userService;
    private NetworkService networkService;

    /**
     * Constructor that creates a controller that accesses the given services
     * @param userService service for User model
     * @param networkService service for Friendship model
     */
    public NetworkController(UserService userService, NetworkService networkService) {
        this.userService = userService;
        this.networkService = networkService;
    }

    /**
     * Adds a new user
     * @param id identifier of the user
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @return empty Optional if the user was added, Optional containing the existing user with the same id otherwise
     */
    public Optional<User> addUser(Long id, String firstName, String lastName){
        return userService.addUserService(id, firstName, lastName);
    }

    /**
     * Removes the user with the given id and his friendships with other users
     * @param id identifier of user
     * @return Optional with the user that was removed, empty Optional if the user didn't exist
     */
    public Optional<User> removeUser(Long id){
        return userService.removeUserService(id);
    }

    /**
     * Finds the user with the given id
     * @param id identifier of the user we want to find
     * @return empty Optional if the user with the given id doesn't exit, Optional with the existing user otherwise
     */
    public Optional<User> findUserById(Long id){
        return userService.findUserByIdService(id);
    }

    /**
     * Updates the user with the given id
     * @param id identifier of the user we want to update
     * @param newFirstName new value for firstName field
     * @param newLastName new value for lastName field
     * @throws InvalidEntityException if the id, newFirstName, newLastName are not valid
     */
    public Optional<User> updateUser(Long id, String newFirstName, String newLastName){
        return userService.updateUserService(id, newFirstName, newLastName);
    }

    public Optional<Friendship> addFriendship(Long idOfFirstUser, Long idOfSecondUser, LocalDateTime date){
        return networkService.addFriendshipService(idOfFirstUser, idOfSecondUser, date);
    }

    /**
     * Removes the friendship between the users with the given identifiers
     * @param idOfFirstUser id of first user
     * @param idOfSecondUser id of second user
     * @return Optional containing the removed relationship, empty Optional if the users are not friends
     */
    public Optional<Friendship> removeFriendship(Long idOfFirstUser, Long idOfSecondUser){
        return networkService.removeFriendshipService(idOfFirstUser, idOfSecondUser);
    }

    /**
     * Finds the friendship between the given users
     * Order of parameters is irrelevant: friendship with id (1, 2) is the same with (2, 1)
     * @param idOfFirstUser identifier of one of the users
     * @param idOfSecondUser identifier of other user
     * @return empty Optional if the friendship doesn't exist, Optional containing the friendship otherwise
     */
    public Optional<Friendship> findFriendship(Long idOfFirstUser, Long idOfSecondUser){
       return networkService.findFriendshipService(idOfFirstUser, idOfSecondUser);
    }

    /**
     * Returns a list with all users and their friends
     * @return list of user, each user containing the list of his friends
     */
    public List<User> getAllUsersAndTheirFriends(){
        return networkService.getAllUsersAndTheirFriendsService();
    }

    /**
     * Computes the number of communities of the network
     * @return number of communities
     */
    public int getNumberOfCommunitiesInNetwork(){
        return networkService.getNumberOfCommunitiesService();
    }

    /**
     * Finds the users of the most social community from the network
     * @return list of the users of the most social community
     */
    public List<User> getMostSocialCommunity(){
        return networkService.getMostSocialCommunityService();
    }

    /**
     * Finds all the friends for a given user
     * @param id identifier of the user we want to find the friends for
     * @return map containing the friends of the given user (as keys) and the date since when they have been friends (as values)
     */
    public Map<Optional<User>, LocalDateTime> findAllFriendsForUser(Long id){
        return userService.findAllFriendsForUserService(id);
    }
}
