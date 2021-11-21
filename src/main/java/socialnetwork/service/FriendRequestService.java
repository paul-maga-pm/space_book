package socialnetwork.service;

import socialnetwork.domain.models.FriendRequest;
import socialnetwork.domain.models.Status;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestService {
    private RepositoryInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepository;
    private EntityValidatorInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestEntityValidator;

    public FriendRequestService(RepositoryInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepository,
                                EntityValidatorInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestEntityValidator) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendRequestEntityValidator = friendRequestEntityValidator;
    }

    public Optional<FriendRequest> sendFriendRequestService(Long idOfFirstUser, Long idOfSecondUser){
        UnorderedPair<Long, Long> idOfNewFriendRequest = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        Optional<FriendRequest> existingFriendRequestOptional = friendRequestRepository.findById(idOfNewFriendRequest);

        if(existingFriendRequestOptional.isEmpty()){
            FriendRequest friendRequest = new FriendRequest(idOfFirstUser, idOfSecondUser, Status.PENDING);
            friendRequestEntityValidator.validate(friendRequest);
            friendRequestRepository.save(friendRequest);
        }
        else
            if(existingFriendRequestOptional.get().getStatus().equals(Status.REJECTED)){
                FriendRequest newFriendRequest = new FriendRequest(idOfFirstUser, idOfSecondUser, Status.PENDING);
            }
        return existingFriendRequestOptional;
    }

    public Optional<FriendRequest> acceptOrRejectFriendRequestService(Long idOfFirstUser, Long idOfSecondUser, Status status){
        UnorderedPair<Long, Long> idOfAcceptedFriendRequest = new UnorderedPair<>(idOfFirstUser, idOfSecondUser);
        Optional<FriendRequest> existingFriendRequestOptional = friendRequestRepository.findById(idOfAcceptedFriendRequest);

        if(existingFriendRequestOptional.isPresent())
            if(existingFriendRequestOptional.get().getStatus().equals(Status.PENDING)){
                FriendRequest acceptedFriendRequest = new FriendRequest(idOfFirstUser, idOfSecondUser, status);
                friendRequestEntityValidator.validate(acceptedFriendRequest);
                friendRequestRepository.update(acceptedFriendRequest);
            }

        return existingFriendRequestOptional;
    }

    public List<FriendRequest> getAllFriendRequestsForUserService(Long idOfUser){
        List<FriendRequest> friendRequests = friendRequestRepository.getAll();
        List<FriendRequest> friendRequestsForUser = new ArrayList<>();

        friendRequests.forEach(friendRequest -> {
            if(friendRequest.getId().second.equals(idOfUser))
                friendRequestsForUser.add(friendRequest);
        });

        return friendRequestsForUser;
    }

    public void removeAllFriendRequestsOfUserService(Long idOfUser){
        List<FriendRequest> friendRequests = friendRequestRepository.getAll();

        friendRequests.forEach(friendRequest -> {
            if(friendRequest.hasUser(idOfUser))
                friendRequestRepository.remove(friendRequest.getId());
        });
    }
}
