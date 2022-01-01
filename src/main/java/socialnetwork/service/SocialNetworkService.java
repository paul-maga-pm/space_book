package socialnetwork.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import socialnetwork.domain.entities.*;
import socialnetwork.utils.containers.PasswordEncryptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

public class SocialNetworkService {
    private UserService userService;
    private NetworkService networkService;
    private FriendRequestService friendRequestService;
    private ConversationService conversationService;
    private EventService eventService;

    public SocialNetworkService(UserService userService,
                                NetworkService networkService,
                                FriendRequestService friendRequestService,
                                ConversationService conversationService,
                                EventService eventService) {
        this.userService = userService;
        this.networkService = networkService;
        this.friendRequestService = friendRequestService;
        this.conversationService = conversationService;
        this.eventService = eventService;
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

    public Event addEventService(String name, String description, LocalDate date, String imageFile){
        return eventService.addEvent(name, description, date, imageFile);
    }

    public List<Event> getAllEventsService(){
        return eventService.getAllEvents();
    }

    public Optional<EventParticipant> findOneEventParticipantService(Long userId, Long eventId){
        return eventService.findOneEventParticipant(userId, eventId);
    }

    public EventParticipant addEventParticipantService(Long userId, Long eventId){
        return eventService.addEventParticipant(userId, eventId);
    }

    public Optional<EventParticipant> removeEventParticipantService(Long userId, Long eventId){
        return eventService.removeEventParticipant(userId, eventId);
    }

    public List<Event> getAllEventsThatAreCloseToCurrentDateForUser(Long userId){
        List<Event> events = eventService.getAllEvents();
        List<Event> closeEvents = new ArrayList<>();

        for(Event event: events){
            Optional<EventParticipant> eventParticipant = eventService.findOneEventParticipant(userId, event.getId());
            if(eventParticipant.isPresent()){
                LocalDate eventDate = event.getDate();
                long days = ChronoUnit.DAYS.between(LocalDate.now(), eventDate);
                if(days <= 5 && (eventDate.isAfter(LocalDate.now()) || eventDate.equals(LocalDate.now())))
                    closeEvents.add(event);
            }
        }

        return closeEvents;
    }

    public List<MessageDto> getMessagesReceivedByUserSentInMonth(Long userId, int month){
        List<Message> messageList = conversationService.getMessagesReceivedByUserSentInMonth(userId, month);
        List<MessageDto> messageDtos = new ArrayList<>();

        for(var msg : messageList){
            var sender = userService.findUserById(msg.getSenderId()).get();
            var dto = new MessageDto(sender, msg.getText(), msg.getDate());
            messageDtos.add(dto);
        }
        return messageDtos;
    }

    public List<FriendshipDto> getAllNewFriendshipsOfUserFromMonth(Long userId, int month){
        return networkService.getAllNewFriendshipsOfUserFromMonth(userId, month);
    }

    public void exportNewFriendsAndNewMessagesOfUserFromMonth(String fileUrl, Long userId, int month) throws IOException {
        var messagesFromMonth = conversationService.getMessagesReceivedByUserSentInMonth(userId, month);
        var newFriendshipsFromMonth = networkService.getAllNewFriendshipsOfUserFromMonth(userId, month);

        PDDocument document = new PDDocument();
        exportToPdfDocumentEntities(document, messagesFromMonth, Message::getText, 10);
        exportToPdfDocumentEntities(document, newFriendshipsFromMonth, FriendshipDto::toString, 10);
        document.save(fileUrl);
        document.close();
    }

    public void exportMessagesReceivedByUserSentByOtherUserInMonth(String fileUrl,
                                                                   Long receiverId,
                                                                   Long senderId,
                                                                   int month) throws IOException {
        List<Message> messages = conversationService.getMessagesReceivedByUserSentByOtherUserInMonth(receiverId,
                senderId,
                month);

        PDDocument doc = new PDDocument();
        exportToPdfDocumentEntities(doc, messages, Message::getText, 5);
        doc.save(fileUrl);
        doc.close();
    }

    private <E> void exportToPdfDocumentEntities(PDDocument document,
                                                 List<E> entities,
                                                 Function<E, String> entityToDocumentLineFunction,
                                                 int entitiesPerPageCount) throws IOException {
        int pagesCount = 1;

        if (entities.size() > 10){
            pagesCount = entities.size() / entitiesPerPageCount;

            if (entities.size() % entitiesPerPageCount != 0)
                pagesCount++;
        }

        for(int i = 0; i < pagesCount; i++){
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(document, page);

            stream.beginText();
            stream.setLeading(14.5f);
            stream.newLineAtOffset(25, 700);
            stream.setFont(PDType1Font.COURIER, 14);

            for(int j = i * entitiesPerPageCount; j <  entities.size() && j < (i + 1) * entitiesPerPageCount; j++) {
                var entity = entities.get(j);
                stream.showText(entityToDocumentLineFunction.apply(entity));
                stream.newLine();
            }
            stream.endText();
            stream.close();
        }
    }
}
