package socialnetwork.service;


import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UndirectedGraph;
import socialnetwork.utils.containers.UnorderedPair;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

/**
 * Business layer for Friendship model
 */
public class NetworkService {
    private RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepository;
    private RepositoryInterface<Long, User> userRepository;
    private EntityValidatorInterface<UnorderedPair<Long, Long>, Friendship> friendshipValidator;

    /**
     * Constructor that creates a new service that accesses the given repositories and validates the friendships
     * with the given validator's rules
     * @param friendshipRepository repository of friendship objects
     * @param userRepository repository of user objects
     * @param friendshipValidator validator for Friendship model
     */

    public NetworkService(RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepository,
                          RepositoryInterface<Long, User> userRepository, EntityValidatorInterface<UnorderedPair<Long, Long>, Friendship> friendshipValidator){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipValidator = friendshipValidator;

    }

    /**
     * Adds a new friendship between the users with the given identifiers
     * @param idOfFirstUser id of first user
     * @param idOfSecondUser id of second user
     * @return empty Optional if the relationship was added, empty Optional if the relationship already exists
     * @throws InvalidEntityException if one of the users is not found in the repository
     */
    public Optional<Friendship> addFriendshipService(Long idOfFirstUser, Long idOfSecondUser){
        UnorderedPair<Long, Long> idOfNewFriendship = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        Optional<Friendship> existingFriendshipOptional = friendshipRepository.findById(idOfNewFriendship);

        if(existingFriendshipOptional.isEmpty()){
            Friendship friendship = new Friendship(idOfFirstUser, idOfSecondUser);
            friendshipValidator.validate(friendship);
            friendshipRepository.save(friendship);
        }
        return existingFriendshipOptional;
    }

    /**
     * Removes the friendship between the users with the given identifiers
     * @param idOfFirstUser id of first user
     * @param idOfSecondUser id of second user
     * @return Optional containing the removed relationship, empty Optional if the users are not friends
     */
    public Optional<Friendship> removeFriendshipService(Long idOfFirstUser, Long idOfSecondUser){
        UnorderedPair<Long, Long> idOfFriendship = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        return friendshipRepository.remove(idOfFriendship);
    }

    /**
     * Computes the number of communities of the network
     * @return number of communities
     */
    public int getNumberOfCommunitiesService(){
        UndirectedGraph<Long> graphOfUserNetwork = new UndirectedGraph<>();

        for(User user : userRepository.getAll())
            graphOfUserNetwork.addVertex(user.getId());

        for(Friendship friendship : friendshipRepository.getAll())
            graphOfUserNetwork.addEdge(friendship.getId().first, friendship.getId().second);

        return graphOfUserNetwork.findNumberOfConnectedComponents();
    }

    /**
     * Finds the users of the most social community from the network
     * @return list of the users of the most social community
     */
    public List<User> getMostSocialCommunityService(){
        UndirectedGraph<User> graphOfUsers = new UndirectedGraph<>(userRepository.getAll());

        for(Friendship friendship : friendshipRepository.getAll()) {
            User user1 = userRepository.findById(friendship.getId().first).get();
            User user2 = userRepository.findById(friendship.getId().second).get();
            graphOfUsers.addEdge(user1, user2);
        }

        return graphOfUsers.findConnectedComponentWithLongestWalk().getVertices();
    }

    /**
     * Returns a list with all users and their friends
     * @return list of user, each user containing the list of his friends
     */
    public List<User> getAllUsersAndTheirFriendsService() {
        List<Friendship> allFriendships = friendshipRepository.getAll();
        UndirectedGraph<User> userUndirectedGraph = new UndirectedGraph<>(userRepository.getAll());

        for(Friendship friendship : allFriendships){
            User user1 = userRepository.findById(friendship.getId().first).get();
            User user2 = userRepository.findById(friendship.getId().second).get();
            userUndirectedGraph.addEdge(user1, user2);
        }
        List<User> allUsersAndTheirFriends = new ArrayList<>(userRepository.getAll());
        for (User currentUser : allUsersAndTheirFriends) {
            currentUser.setFriendsList(userUndirectedGraph.getNeighboursOf(currentUser).stream().toList());
        }
        return allUsersAndTheirFriends;
    }

}
