package socialnetwork.service;

import socialnetwork.domain.models.FriendRequest;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.Status;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Business layer for FriendRequest model
 */
public class FriendRequestService {
    private RepositoryInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepository;
    private RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepository;
    private EntityValidatorInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestValidator;

    /**
     * Constructor that creates a new socialnetwork.service that accesses the given repositories and validates the friendsRequests
     * with the given validator's rules
     * @param friendRequestRepository repository of friendRequests objects
     * @param friendshipRepository repository of friendships objects
     * @param friendRequestValidator validator for FriendRequest model
     */
    public FriendRequestService(RepositoryInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepository,
                                RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepository,
                                EntityValidatorInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestValidator) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendRequestValidator = friendRequestValidator;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Adds a new friendRequest between the users with the given identifiers if it does not exist
     * or if it exists and has a REJECTED status
     * @param idOfFirstUser identifier of the first user
     * @param idOfSecondUser identifier of the second user
     * @return empty Optional if the friendRequest did not exist before
     *         Optional containing the existing friendRequest otherwise
     */
    public Optional<FriendRequest> sendFriendRequestService(Long idOfFirstUser, Long idOfSecondUser){
        UnorderedPair<Long, Long> idOfNewFriendRequest = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        Optional<FriendRequest> existingFriendRequestOptional = friendRequestRepository.findById(idOfNewFriendRequest);

        if(existingFriendRequestOptional.isEmpty()){
            FriendRequest friendRequest = new FriendRequest(idOfFirstUser, idOfSecondUser, Status.PENDING, LocalDateTime.now());
            friendRequestValidator.validate(friendRequest);
            friendRequestRepository.save(friendRequest);
        }
        else
        if(existingFriendRequestOptional.get().getStatus().equals(Status.REJECTED)){
            FriendRequest newFriendRequest = new FriendRequest(idOfFirstUser, idOfSecondUser, Status.PENDING, LocalDateTime.now());
            friendRequestRepository.update(newFriendRequest);
        }
        return existingFriendRequestOptional;
    }

    /**
     * Modify the status of an existing PENDING friendRequest based on the new status
     * @param idOfFirstUser identifier of the first user
     * @param idOfSecondUser identifier of the second user
     * @param status new status of the friendRequest
     * @return empty Optional if the friendRequest did not exist before
     *         Optional containing the existing friendRequest otherwise
     */
    public Optional<FriendRequest> acceptOrRejectFriendRequestService(Long idOfFirstUser,
                                                                      Long idOfSecondUser,
                                                                      Status status){
        UnorderedPair<Long, Long> idOfAcceptedFriendRequest = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        Optional<FriendRequest> existingFriendRequestOptional = friendRequestRepository.findById(idOfAcceptedFriendRequest);

        if(existingFriendRequestOptional.isPresent())
            if(existingFriendRequestOptional.get().getStatus().equals(Status.PENDING)){
                LocalDateTime date = existingFriendRequestOptional.get().getDate();
                FriendRequest newFriendRequest = new FriendRequest(idOfFirstUser, idOfSecondUser, status, date);
                if(status.equals(Status.APPROVED))
                    friendshipRepository.save(new Friendship(idOfFirstUser, idOfSecondUser, LocalDateTime.now()));
                friendRequestRepository.update(newFriendRequest);
            }

        return existingFriendRequestOptional;
    }

    /**
     * Modify the status of a friendRequest into REJECTED when its corresponding friendships has been removed
     * @param idOfFirstUser identifier of the first user
     * @param idOfSecondUser identifier of the second user
     */
    public void rejectADeletedFriendship(Long idOfFirstUser, Long idOfSecondUser){
        Optional<FriendRequest> existingFriendRequestOptional =
                friendRequestRepository.findById(new UnorderedPair<>(idOfFirstUser, idOfSecondUser));
        if(existingFriendRequestOptional.isPresent()){
            FriendRequest friendRequest = existingFriendRequestOptional.get();
            friendRequest.setStatus(Status.REJECTED);
            friendRequestRepository.update(friendRequest);
        }
    }

    /**
     * Get all friendRequests that were sent to the user with the given id
     * @param idOfUser identifier of the user
     * @return lists of all the friendRequests sent to the user with the given id
     */
    public List<FriendRequest> getAllFriendRequestsForUserService(Long idOfUser){
        List<FriendRequest> friendRequests = friendRequestRepository.getAll();
        List<FriendRequest> friendRequestsForUser = new ArrayList<>();

        friendRequests.forEach(friendRequest -> {
            if(friendRequest.getId().second.equals(idOfUser))
                friendRequestsForUser.add(friendRequest);
        });

        return friendRequestsForUser;
    }

    /**
     * Remove all friendRequests for which the user with the given id is a part of
     * @param idOfUser identifier of the user
     */
    public void removeAllFriendRequestsOfUserService(Long idOfUser){
        List<FriendRequest> friendRequests = friendRequestRepository.getAll();

        friendRequests.forEach(friendRequest -> {
            if(friendRequest.hasUser(idOfUser))
                friendRequestRepository.remove(friendRequest.getId());
        });
    }
}
