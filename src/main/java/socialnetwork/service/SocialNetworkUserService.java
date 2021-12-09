package socialnetwork.service;

import socialnetwork.domain.models.FriendRequest;
import socialnetwork.domain.models.Friendship;

import java.util.List;
import java.util.Optional;

public class SocialNetworkUserService {
    private UserService userService;
    private NetworkService networkService;
    private ConversationService conversationService;
    private FriendRequestService friendRequestService;

    private Long idOfLoggedUser;

    public SocialNetworkUserService(UserService userService,
                                    NetworkService networkService,
                                    ConversationService conversationService,
                                    FriendRequestService friendRequestService) {
        this.userService = userService;
        this.networkService = networkService;
        this.conversationService = conversationService;
        this.friendRequestService = friendRequestService;
    }

    public void signUpUserService(String firstName, String lastName, String userName, String password){
        idOfLoggedUser = userService.signUpUser(firstName, lastName, userName, password);
    }

    public void loginUserService(String userName, String password){
        idOfLoggedUser = userService.loginUser(userName, password);
    }

    public void sendFriendRequestService(String friendUserName){
        Long idOFriend = userService.findIdOfUserWithUsername(friendUserName);
        friendRequestService.sendFriendRequestService(idOfLoggedUser, idOFriend);
    }

    public Optional<Friendship> removeFriendshipService(String friendUsername){
        Long idOfFriend = userService.findIdOfUserWithUsername(friendUsername);
        Optional<Friendship> existingFriendshipOptional = networkService.removeFriendshipService(idOfLoggedUser, idOfFriend);
        if(existingFriendshipOptional.isPresent())
            friendRequestService.rejectADeletedFriendship(existingFriendshipOptional.get().getId().first,
                    existingFriendshipOptional.get().getId().second);
        return existingFriendshipOptional;
    }

    public List<FriendRequest> getAllFriendRequestsOfUser(){
        return friendRequestService.getAllFriendRequestsForUserService(idOfLoggedUser);
    }
}
