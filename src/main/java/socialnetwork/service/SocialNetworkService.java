package socialnetwork.service;

import socialnetwork.domain.entities.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SocialNetworkService {
    private UserService userService;
    private NetworkService networkService;
    private FriendRequestService friendRequestService;
    private ConversationService conversationService;
    private Long loggedUserId;
    private User loggedUser;

    public SocialNetworkService(UserService userService,
                                NetworkService networkService,
                                FriendRequestService friendRequestService,
                                ConversationService conversationService) {
        this.userService = userService;
        this.networkService = networkService;
        this.friendRequestService = friendRequestService;
        this.conversationService = conversationService;
    }

    public Long getLoggedUserId(){
        return loggedUserId;
    }

    public User getLoggedUser(){
        return loggedUser;
    }

    public void signUpUserService(String firstName, String lastName, String userName, String password){
        loggedUser = userService.signUpUser(firstName, lastName, userName, password);
        loggedUserId = loggedUser.getId();
        loggedUser.setUserName(userName);
    }

    public void loginUserService(String userName, String password){
        loggedUser = userService.loginUser(userName, password);
        loggedUserId = loggedUser.getId();
    }

    public void sendFriendRequestService(Long idOfFriend){
        friendRequestService.sendFriendRequestService(loggedUserId, idOfFriend);
    }

    public Optional<FriendRequest> acceptOrRejectFriendRequestService(Long idOfFriend, Status status){
        return friendRequestService.acceptOrRejectFriendRequestService(idOfFriend, loggedUserId, status);
    }

    public Optional<Friendship> removeFriendshipService(Long idOfFriend){
        Optional<Friendship> existingFriendshipOptional = networkService.removeFriendshipService(loggedUserId, idOfFriend);
        if(existingFriendshipOptional.isPresent())
            friendRequestService.rejectADeletedFriendship(existingFriendshipOptional.get().getId().first,
                    existingFriendshipOptional.get().getId().second);
        return existingFriendshipOptional;
    }

    public Optional<FriendRequest> withdrawFriendRequest(Long idOfFriend){
        return friendRequestService.removeFriendRequestService(loggedUserId, idOfFriend);
    }

    public Optional<FriendRequest> findOneFriendRequestService(Long idOfFriend){
        return friendRequestService.findOneFriendRequest(loggedUserId, idOfFriend);
    }

    public Map<FriendRequest, User> getAllFriendRequestsOfLoggedUser(){
        List<FriendRequest> friendRequestsForUser = friendRequestService.getAllFriendRequestsForUserService(loggedUserId);
        List<User> users = userService.getAllUsers();
        Map<FriendRequest, User>  friendRequestsAndSendersForLoggedUser = new HashMap<>();
        for(FriendRequest friendRequest: friendRequestsForUser){
            Optional<User> sender = users.stream().filter(user -> user.getId()==friendRequest.getId().first).findFirst();
            friendRequestsAndSendersForLoggedUser.put(friendRequest, sender.get());
        }
        return friendRequestsAndSendersForLoggedUser;
    }

    public Map<Optional<User>, LocalDateTime> findAllFriendsOfLoggedUser(){
        Map<Optional<User>, LocalDateTime> friends = networkService.findAllFriendsForUserService(loggedUserId);

        return friends;
    }

    public List<User> findUsersThatHaveInTheirFullNameTheString(String str){
        return userService.findUsersThatHaveInTheirFullNameTheString(str);
    }

    public ConversationDto createConversation(String conversationName,
                                              String conversationDescription,
                                              List<Long> participantsIdWithoutLoggedUser){
        participantsIdWithoutLoggedUser.add(loggedUserId);
        return conversationService.createConversation(conversationName,
                conversationDescription,
                participantsIdWithoutLoggedUser);
    }

    public void sendMessageInConversation(Long conversationId, String text, LocalDateTime date){
        conversationService.sendMessageInConversation(conversationId, loggedUserId, text, date);
    }

    public List<ConversationDto> getLoggedUsersConversations(){
        return conversationService.getConversationsOfUser(loggedUserId);
    }
}
