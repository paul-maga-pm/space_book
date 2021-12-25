package socialnetwork.service;

import socialnetwork.domain.entities.*;

import java.time.LocalDateTime;
import java.util.*;

public class SocialNetworkService {
    private UserService userService;
    private NetworkService networkService;
    private FriendRequestService friendRequestService;
    private ConversationService conversationService;

    public SocialNetworkService(UserService userService,
                                NetworkService networkService,
                                FriendRequestService friendRequestService,
                                ConversationService conversationService) {
        this.userService = userService;
        this.networkService = networkService;
        this.friendRequestService = friendRequestService;
        this.conversationService = conversationService;
    }

    public User signUpUserService(String firstName, String lastName, String userName, String password){
        User signedUser = userService.signUpUser(firstName, lastName, userName, password);
        signedUser.setUserName(userName);
        return signedUser;
    }

    public User loginUserService(String userName, String password){
        return userService.loginUser(userName, password);
    }

    public void sendFriendRequestService(Long senderId, Long receiverId){
        friendRequestService.sendFriendRequestService(senderId, receiverId);
    }

    public Optional<FriendRequest> acceptOrRejectFriendRequestService(Long firstUserId, Long secondUserId, Status status){
        return friendRequestService.acceptOrRejectFriendRequestService(firstUserId, secondUserId, status);
    }

    public Optional<Friendship> removeFriendshipService(Long firstUserId, Long secondUserId){
        Optional<Friendship> existingFriendshipOptional = networkService.removeFriendshipService(firstUserId, secondUserId);
        if(existingFriendshipOptional.isPresent())
            friendRequestService.rejectADeletedFriendship(existingFriendshipOptional.get().getId().first,
                    existingFriendshipOptional.get().getId().second);
        return existingFriendshipOptional;
    }

    public Optional<FriendRequest> withdrawFriendRequest(Long senderId, Long receiverId){
        return friendRequestService.removeFriendRequestService(senderId, receiverId);
    }

    public Optional<FriendRequest> findOneFriendRequestService(Long senderId, Long receiverId){
        return friendRequestService.findOneFriendRequest(senderId, receiverId);
    }

    public List<FriendRequestDto> getAllFriendRequestsSentToUser(Long receiverId){
        List<FriendRequestDto> friendRequestDtoList = new ArrayList<>();
        List<FriendRequest> friendRequestsForUser = friendRequestService.getAllFriendRequestsReceivedByUser(receiverId);
        List<User> users = userService.getAllUsers();
        for(FriendRequest friendRequest: friendRequestsForUser){
            Optional<User> sender = users.stream()
                    .filter(user -> user.getId()==friendRequest.getSenderId()).findFirst();
            FriendRequestDto dto = new FriendRequestDto(friendRequest, sender.get());
            friendRequestDtoList.add(dto);
        }
        return friendRequestDtoList;
    }

    public List<FriendRequestDto> getAllFriendRequestsSentByUser(Long senderId){
        List<FriendRequestDto> friendRequestDtoList = new ArrayList<>();
        List<FriendRequest> friendRequestsSentByUser = friendRequestService.getAllFriendRequestsSentByUser(senderId);
        User sender = userService.findUserById(senderId).get();
        Map<Long, User> allUsersMap = new HashMap<>();

        for(var user : userService.getAllUsers())
            allUsersMap.put(user.getId(), user);

        for(var request : friendRequestsSentByUser){
            var dto = new FriendRequestDto(request, sender);
            var receiver = allUsersMap.get(request.getReceiverId());
            dto.setReceiver(receiver);
            friendRequestDtoList.add(dto);
        }
        return friendRequestDtoList;
    }

    public List<FriendshipDto> findAllFriendsOfUser(Long userId){
        return networkService.findAllFriendsForUserService(userId);
    }

    public List<User> findUsersThatHaveInTheirFullNameTheString(String str){
        return userService.findUsersThatHaveInTheirFullNameTheString(str);
    }

    public ConversationDto createConversation(Long loggedUserId,
                                              String conversationName,
                                              String conversationDescription,
                                              List<Long> participantsIdWithoutLoggedUser){
        participantsIdWithoutLoggedUser.add(loggedUserId);
        return conversationService.createConversation(conversationName,
                conversationDescription,
                participantsIdWithoutLoggedUser);
    }

    public void sendMessageInConversation(Long senderId, Long conversationId, String text, LocalDateTime date){
        conversationService.sendMessageInConversation(conversationId, senderId, text, date);
    }

    public LocalDateTime findDateOfFriendship(Long firstUserId, Long secondUserId){
        return networkService.findDateOfFriendship(firstUserId, secondUserId);
    }

    public List<ConversationDto> getConversationsOfUser(Long userId){
        return conversationService.getConversationsOfUser(userId);
    }

    public int countAcceptedFriendRequestsSentByUser(Long senderId) {
        int count = 0;

        for(var friendRequest : friendRequestService.getAllFriendRequestsSentByUser(senderId))
            if (friendRequest.getStatus() == Status.APPROVED)
                count ++;
        return count;
    }

    public int countFriendRequestsReceivedByUser(Long receiverId) {
        return friendRequestService.getAllFriendRequestsReceivedByUser(receiverId).size();
    }
}
