package socialnetwork.service;


import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.FriendshipDto;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.EntityValidator;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.containers.UndirectedGraph;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Business layer for Friendship model
 */
public class NetworkService {
    private Repository<UnorderedPair<Long, Long>, Friendship> friendshipRepository;
    private Repository<Long, User> userRepository;
    private EntityValidator<UnorderedPair<Long, Long>, Friendship> friendshipValidator;

    /**
     * Constructor that creates a new socialnetwork.service that accesses the given repositories and validates the friendships
     * with the given validator's rules
     * @param friendshipRepository repository of friendship objects
     * @param userRepository repository of user objects
     * @param friendshipValidator validator for Friendship model
     */

    public NetworkService(Repository<UnorderedPair<Long, Long>, Friendship> friendshipRepository,
                          Repository<Long, User> userRepository, EntityValidator<UnorderedPair<Long, Long>, Friendship> friendshipValidator){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipValidator = friendshipValidator;

    }

    /**
     * Adds a new friendship between the users with the given identifiers
     * @param idOfFirstUser id of first user
     * @param idOfSecondUser id of second user
     * @param date LocalDateTime of when the friendship was created
     * @return empty Optional if the relationship was added, empty Optional if the relationship already exists
     * @throws InvalidEntityException if one of the users is not found in the repository
     */
    public Optional<Friendship> addFriendshipService(Long idOfFirstUser, Long idOfSecondUser, LocalDateTime date){
        UnorderedPair<Long, Long> idOfNewFriendship = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        Optional<Friendship> existingFriendshipOptional = friendshipRepository.findById(idOfNewFriendship);

        if(existingFriendshipOptional.isEmpty()){
            Friendship friendship = new Friendship(idOfFirstUser, idOfSecondUser, date);
            friendshipValidator.validate(friendship);
            friendshipRepository.save(friendship);
        }
        return existingFriendshipOptional;
    }

    /**
     * Removes the friendship between the users with the given identifiers
     * Order of parameters is irrelevant: friendship with id (1, 2) is the same with (2, 1)
     * @param idOfFirstUser id of first user
     * @param idOfSecondUser id of second user
     * @return Optional containing the removed relationship, empty Optional if the users are not friends
     */
    public Optional<Friendship> removeFriendshipService(Long idOfFirstUser, Long idOfSecondUser){
        UnorderedPair<Long, Long> idOfFriendship = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        return friendshipRepository.remove(idOfFriendship);
    }

    /**
     * Finds the friendship between the given users
     * Order of parameters is irrelevant: friendship with id (1, 2) is the same with (2, 1)
     * @param idOfFirstUser identifier of one of the users
     * @param idOfSecondUser identifier of other user
     * @return empty Optional if the friendship doesn't exist, Optional containing the friendship otherwise
     */
    public Optional<Friendship> findFriendshipService(Long idOfFirstUser, Long idOfSecondUser){
        UnorderedPair<Long, Long> id = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        return friendshipRepository.findById(id);
    }

    public void removeAllFriendshipsOfUserService(Long idOfUser){
        List<Friendship> friendships = friendshipRepository.getAll();

        friendships.forEach((friendship) -> {
            if(friendship.hasUser(idOfUser))
                friendshipRepository.remove(friendship.getId());
        });
    }

    public List<FriendshipDto> findAllFriendsForUserService(Long id){
        List<FriendshipDto> friendshipDtoList = new ArrayList<>();
        List<Friendship> friendships = friendshipRepository.getAll();
        friendships.stream().filter(friendship -> friendship.hasUser(id))
                .forEach(friendship -> {
                    Long idOfFriend;
                    if(friendship.getId().first == id)
                        idOfFriend = friendship.getId().second;
                    else
                        idOfFriend = friendship.getId().first;
                    User friend = userRepository.findById(idOfFriend).get();
                    LocalDateTime friendshipDate = friendship.getDate();
                    FriendshipDto dto = new FriendshipDto(friend, friendshipDate);
                    friendshipDtoList.add(dto);
                });
        return friendshipDtoList;
    }


    public List<FriendshipDto> getAllNewFriendshipsOfUserFromYearAndMonth(Long userId, int year, int month) {
        List<FriendshipDto> usersFriendshipsDtoList = findAllFriendsForUserService(userId);
        return  usersFriendshipsDtoList.stream()
                .filter(dto -> dto.getFriendshipDate().getYear() == LocalDateTime.now().getYear() &&
                        dto.getFriendshipDate().getMonth().getValue() == month)
                .collect(Collectors.toList());
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

    public LocalDateTime findDateOfFriendship(Long firstUserId, Long secondUserId) {
        var friendship = friendshipRepository.findById(new UnorderedPair<>(firstUserId, secondUserId));
        if (friendship.isEmpty())
            return null;
        return friendship.get().getDate();
    }
}
