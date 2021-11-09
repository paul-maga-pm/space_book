package socialnetwork.controllers;


import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.service.NetworkService;
import socialnetwork.service.UserService;

import java.util.List;
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
     * Adds a new friendship between the users with the given identifiers
     * @param idOfFirstUser id of first user
     * @param idOfSecondUser id of second user
     * @return empty Optional if the relationship was added, empty Optional if the relationship already exists
     * @throws InvalidEntityException if one of the users is not found in the repository
     */
    public Optional<Friendship> addFriendship(Long idOfFirstUser, Long idOfSecondUser){
        return networkService.addFriendshipService(idOfFirstUser, idOfSecondUser);
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
}
