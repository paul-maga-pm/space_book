package socialnetwork.service;


import socialnetwork.domain.models.*;
import socialnetwork.exceptions.InvalidEntityException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller between ui and business layer
 */
public class SocialNetworkAdminService {
    private AdminService adminService;
    private NetworkService networkService;
    private ConversationService conversationService;
    private FriendRequestService friendRequestService;

    /**
     * Constructor that creates a controller that accesses the given services
     * @param adminService socialnetwork.service for User model
     * @param networkService socialnetwork.service for Friendship model
     */
    public SocialNetworkAdminService(AdminService adminService, NetworkService networkService,
                                     ConversationService conversationService, FriendRequestService friendRequestService) {
        this.adminService = adminService;
        this.networkService = networkService;
        this.conversationService = conversationService;
        this.friendRequestService = friendRequestService;
    }

    /**
     * Adds a new user
     * @param id identifier of the user
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @return empty Optional if the user was added, Optional containing the existing user with the same id otherwise
     */
    public Optional<User> addUser(Long id, String firstName, String lastName){
        return adminService.addUserService(id, firstName, lastName);
    }

    /**
     * Removes the user with the given id and all dependencies related to the user
     * @param id identifier of user
     * @return Optional with the user that was removed, empty Optional if the user didn't exist
     */
    public Optional<User> removeUser(Long id){
        networkService.removeAllFriendshipsOfUserService(id);
        conversationService.removeAllConversationsOfUserService(id);
        friendRequestService.removeAllFriendRequestsOfUserService(id);
        return adminService.removeUserService(id);
    }

    /**
     * Finds the user with the given id
     * @param id identifier of the user we want to find
     * @return empty Optional if the user with the given id doesn't exit, Optional with the existing user otherwise
     */
    public Optional<User> findUserById(Long id){
        return adminService.findUserByIdService(id);
    }

    /**
     * Updates the user with the given id
     * @param id identifier of the user we want to update
     * @param newFirstName new value for firstName field
     * @param newLastName new value for lastName field
     * @throws InvalidEntityException if the id, newFirstName, newLastName are not valid
     */
    public Optional<User> updateUser(Long id, String newFirstName, String newLastName){
        return adminService.updateUserService(id, newFirstName, newLastName);
    }

    public Optional<Friendship> addFriendship(Long idOfFirstUser, Long idOfSecondUser, LocalDateTime date){
        return networkService.addFriendshipService(idOfFirstUser, idOfSecondUser, date);
    }

    /**
     * Removes the friendship between the users with the given identifiers
     * @param idOfFirstUser id of first user
     * @param idOfSecondUser id of second user
     * @return Optional containing the removed relationship, empty Optional if the users are not friends
     */
    public Optional<Friendship> removeFriendship(Long idOfFirstUser, Long idOfSecondUser){
        Optional<Friendship> existingFriendshipOptional = networkService.removeFriendshipService(idOfFirstUser, idOfSecondUser);
        if(existingFriendshipOptional.isPresent())
            friendRequestService.rejectADeletedFriendship(existingFriendshipOptional.get().getId().first,
                    existingFriendshipOptional.get().getId().second);
        return existingFriendshipOptional;
    }

    /**
     * Finds the friendship between the given users
     * Order of parameters is irrelevant: friendship with id (1, 2) is the same with (2, 1)
     * @param idOfFirstUser identifier of one of the users
     * @param idOfSecondUser identifier of other user
     * @return empty Optional if the friendship doesn't exist, Optional containing the friendship otherwise
     */
    public Optional<Friendship> findFriendship(Long idOfFirstUser, Long idOfSecondUser){
       return networkService.findFriendshipService(idOfFirstUser, idOfSecondUser);
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

    /**
     * Finds all the friends for a given user
     * @param id identifier of the user we want to find the friends for
     * @return map containing the friends of the given user (as keys) and the date since when they have been friends (as values)
     */
    public Map<Optional<User>, LocalDateTime> findAllFriendsForUser(Long id){
        return networkService.findAllFriendsForUserService(id);
    }

    public Map<Optional<User>, LocalDateTime> findAllFriendsForUserFromMonth(Long idOfUser, int month){
        return networkService.findAllFriendsForUserFromMonthService(idOfUser, month);
    }

    /**
     * Add a new message
     * @param senderId identifier of sender
     * @param receiverIds identifiers of receivers
     * @param text content of the message
     * @param date date when the message was sent
     */
    public void sendMessageFromUserTo(Long senderId, List<Long> receiverIds, String text, LocalDateTime date){
        conversationService.sendMessageFromUserToService(senderId, receiverIds, text, date);
    }

    /**
     * Add a new reply
     * @param messageRepliedToId identifier of the message that we reply to
     * @param senderId identifier of the sender
     * @param text content of the reply
     * @param date date when the reply was sent
     * @throws InvalidEntityException if there is no message with messageRepliedToId or
     *                                if senderId is not part of the receivers of the message with messageRepliedToId
     */
    public void replyToMessage(Long messageRepliedToId, Long senderId, String text, LocalDateTime date){
        conversationService.replyToMessageService(messageRepliedToId, senderId, text, date);
    }

    /**
     * Get conversation between two users
     * @param idOfFirstUser identifier of the first user that is part of the conversation
     * @param idOfSecondUser identifier of the second user that is part of the conversation
     * @return the conversation (list of messages) between the two users
     */
    public List<MessageReadModel> getConversationBetweenTwoUsers(Long idOfFirstUser, Long idOfSecondUser){
        return conversationService.getConversationBetweenTwoUsersService(idOfFirstUser, idOfSecondUser);
    }

    /**
     * Adds a new friendRequest between the users with the given identifiers if it does not exist
     * or if it exists and has a REJECTED status
     * @param idOfFirstUser identifier of the first user
     * @param idOfSecondUser identifier of the second user
     * @return empty Optional if the friendRequest did not exist before
     *         Optional containing the existing friendRequest otherwise
     */
    public Optional<FriendRequest> sendFriendRequest(Long idOfFirstUser, Long idOfSecondUser){
        return friendRequestService.sendFriendRequestService(idOfFirstUser, idOfSecondUser);
    }

    /**
     * Modify the status of an existing PENDING friendRequest based on the new status
     * @param idOfFirstUser identifier of the first user
     * @param idOfSecondUser identifier of the second user
     * @param status new status of the friendRequest
     * @return empty Optional if the friendRequest did not exist before
     *         Optional containing the existing friendRequest otherwise
     */
    public Optional<FriendRequest> acceptOrRejectFriendRequest(Long idOfFirstUser, Long idOfSecondUser, Status status){
        return friendRequestService.acceptOrRejectFriendRequestService(idOfFirstUser, idOfSecondUser, status);
    }

    /**
     * Get all friendRequests that were sent to the user with the given id
     * @param idOfUser identifier of the user
     * @return lists of all the friendRequests sent to the user with the given id
     */
    public List<FriendRequest> getAllFriendRequestsForUser(Long idOfUser){
        return friendRequestService.getAllFriendRequestsForUserService(idOfUser);
    }
}
