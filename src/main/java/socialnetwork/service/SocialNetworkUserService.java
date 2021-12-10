package socialnetwork.service;

import socialnetwork.domain.models.FriendRequest;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    public void sendFriendRequestService(Long idOfFriend){
        friendRequestService.sendFriendRequestService(idOfLoggedUser, idOfFriend);
    }

    public Optional<Friendship> removeFriendshipService(Long idOfFriend){
        Optional<Friendship> existingFriendshipOptional = networkService.removeFriendshipService(idOfLoggedUser, idOfFriend);
        if(existingFriendshipOptional.isPresent())
            friendRequestService.rejectADeletedFriendship(existingFriendshipOptional.get().getId().first,
                    existingFriendshipOptional.get().getId().second);
        return existingFriendshipOptional;
    }

    public List<FriendRequest> getAllFriendRequestsOfLoggedUser(){
        return friendRequestService.getAllFriendRequestsForUserService(idOfLoggedUser);
    }

    public Map<Optional<User>, LocalDateTime> findAllFriendsOfLoggedUser(){
        Map<Optional<User>, LocalDateTime> friends = networkService.findAllFriendsForUserService(idOfLoggedUser);

        for(var entry : friends.keySet()){
            String userName = userService.findUserNameOfUser(entry.get().getId());
            entry.get().setUserName(userName);
        }

        return friends;
    }
}
