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
     * Returns a list of FriendshipDto's with all the friends of the given user
     */
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


    /**
     * Returns a list with all FriendshipDto-s from the given year and month for the given user
     */
    public List<FriendshipDto> getAllNewFriendshipsOfUserFromYearAndMonth(Long userId, int year, int month) {
        List<FriendshipDto> usersFriendshipsDtoList = findAllFriendsForUserService(userId);
        return  usersFriendshipsDtoList.stream()
                .filter(dto -> dto.getFriendshipDate().getYear() == year &&
                        dto.getFriendshipDate().getMonth().getValue() == month)
                .collect(Collectors.toList());
    }


    /**
     * Returns the date of the friendship between the users
     */
    public LocalDateTime findDateOfFriendship(Long firstUserId, Long secondUserId) {
        var friendship = friendshipRepository.findById(new UnorderedPair<>(firstUserId, secondUserId));
        if (friendship.isEmpty())
            return null;
        return friendship.get().getDate();
    }
}
