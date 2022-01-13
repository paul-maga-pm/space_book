package socialnetwork.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import socialnetwork.domain.entities.*;
import socialnetwork.events.NewConversationHasBeenCreatedEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

public class SocialNetworkService
        implements Observable<NewConversationHasBeenCreatedEvent>{
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

    public void setNumberOfUserPerFiltrationByNamePage(int usersCount){
        userService.setUsersPerPageCount(usersCount);
    }

    public void setCurrentPageIndexOfUserFiltration(int pageIndex){
        userService.setCurrentPageIndex(pageIndex);
    }

    public List<User> getUsersByName(String name, int pageIndex){
        return userService.getUsersByName(name, pageIndex);
    }

    public List<User> getNextUsersByName(String name){
        return userService.getNextUsersByName(name);
    }

    public int getNumberOfUsersThatHaveInTheirNameTheString(String name) {
        return  userService.getNumberOfUsersThatHaveInTheirNameTheString(name);
    }

    public User signUpUserService(String firstName, String lastName, String userName, String password, String profilePictureFile){
        User signedUser = userService.signUpUser(firstName, lastName, userName, password, profilePictureFile);
        signedUser.setUserName(userName);
        return signedUser;
    }

    public User loginUserService(String userName, String password){
        return userService.loginUser(userName, password);
    }

    public Optional<User> updateUserService(User newUser){
        return userService.updateUser(newUser);
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
        var conversation = conversationService.createConversation(conversationName,
                                                                conversationDescription,
                                                                participantsIdWithoutLoggedUser);
        var event = new NewConversationHasBeenCreatedEvent(conversation);
        notifyObservers(event);
        return conversation;
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

    public EventParticipant addEventParticipantService(Long userId, Long eventId, NotificationStatus notificationStatus){
        return eventService.addEventParticipant(userId, eventId, notificationStatus);
    }

    public Optional<EventParticipant> removeEventParticipantService(Long userId, Long eventId){
        return eventService.removeEventParticipant(userId, eventId);
    }

    public Optional<EventParticipant> updateEventParticipantService(EventParticipant newEventParticipant){
        return eventService.updateEventParticipant(newEventParticipant);
    }

    public List<Event> getAllEventsThatAreCloseToCurrentDateForUser(Long userId){
        List<Event> events = eventService.getAllEvents();
        List<Event> closeEvents = new ArrayList<>();

        for(Event event: events){
            Optional<EventParticipant> eventParticipant = eventService.findOneEventParticipant(userId, event.getId());
            if(eventParticipant.isPresent()){
                if(eventParticipant.get().getNotificationStatus().equals(NotificationStatus.SUBSCRIBED)) {
                    LocalDate eventDate = event.getDate();
                    long days = ChronoUnit.DAYS.between(LocalDate.now(), eventDate);
                    if (days <= 5 && (eventDate.isAfter(LocalDate.now()) || eventDate.equals(LocalDate.now())))
                        closeEvents.add(event);
                }
            }
        }

        return closeEvents;
    }


    public List<MessageDto> getMessagesReceivedByUserInYearAndMonth(Long userId, int year, int month){
        List<Message> messageList = conversationService.getMessagesReceivedByUserInYearAndMonth(userId, year,  month);
        List<MessageDto> messageDtos = new ArrayList<>();

        for(var msg : messageList){
            var sender = userService.findUserById(msg.getSenderId()).get();
            var dto = new MessageDto(sender, msg.getText(), msg.getDate());
            messageDtos.add(dto);
        }
        return messageDtos;
    }


    public List<FriendshipDto> getAllNewFriendshipsOfUserFromYearAndMonth(Long userId, int year, int month){
        return networkService.getAllNewFriendshipsOfUserFromYearAndMonth(userId, year, month);
    }



    public void exportNewFriendsAndNewMessagesOFUserFromYearAndMonth(String fileUrl,
                                                                     Long userId,
                                                                     int year,
                                                                     int month) throws IOException {
        var messagesFromMonth = conversationService.getMessagesReceivedByUserInYearAndMonth(userId,
                year,
                month);
        var newFriendshipsFromMonth = networkService.getAllNewFriendshipsOfUserFromYearAndMonth(userId,
                year,
                month);

        int spacesCount = 25;
        Function<Message, String> messageToLine = m -> {
            var sender = userService.findUserById(m.getSenderId()).get();
            var name = sender.getFirstName() + " " + sender.getLastName();
            var fields = Arrays.asList(m.getText(), m.getDate().format(DateTimeFormatter.ISO_DATE), name);
            var columnSizes = Arrays.asList(12, 4, 5);
            return parseToDocLine(fields, columnSizes, spacesCount);
        };

        User user = userService.findUserById(userId).get();
        String usersName = user.getFirstName() + " " + user.getLastName();
        String userHeader = "User: " + usersName;
        LocalDateTime yearAndMonth = LocalDateTime.of(year, month, 1, 1, 1);
        String monthString = yearAndMonth.getMonth().toString();
        String messageReportYearAndMonth = "Messages received in " + monthString + " " + year;
        PDDocument document = new PDDocument();
        exportToPdfDocumentEntities(document,
                Arrays.asList("Activity report", userHeader, messageReportYearAndMonth),
                getColumnsLineForReport(Arrays.asList("Message text", "Date", "Sender"), spacesCount),
                messagesFromMonth,
                messageToLine,
                30);


        Function<FriendshipDto, String> friendshipToLine = f -> {
            var friendName = f.getFriend().getFirstName() + " " + f.getFriend().getLastName();
            var dateStr = f.getFriendshipDate().format(DateTimeFormatter.ISO_DATE);
            var fields = Arrays.asList(friendName, dateStr);
            var sizes = Arrays.asList("Friend".length(), "Date".length());
            return parseToDocLine(fields, sizes, spacesCount);
        };
        String newFriendshipsYearAndMonthStr = "Friend requests accepted in " + monthString + " " + year;
        exportToPdfDocumentEntities(document,
                Arrays.asList("Activity report", userHeader, newFriendshipsYearAndMonthStr),
                getColumnsLineForReport(Arrays.asList("Friend", "Date"), spacesCount),
                newFriendshipsFromMonth,
                friendshipToLine,
                30);
        document.save(fileUrl);
        document.close();
    }

    private String getColumnsLineForReport(List<String> columnNamesList, int spacesBetweenColumns){
        String columnNames = columnNamesList.get(0);

        for(int j = 1; j < columnNamesList.size(); j++) {
            for (int i = 0; i < spacesBetweenColumns; i++)
                columnNames = columnNames.concat(" ");

            columnNames = columnNames.concat(columnNamesList.get(j));
        }
        return columnNames;
    }



    private String parseToDocLine(List<String> fields, List<Integer> columnSizes, int spacesCount){
        String line = "";

        for (int i = 0; i < fields.size(); i++){
            String field = fields.get(i);
            int size = columnSizes.get(i);

            line = line.concat(field);

            int spaces = spacesCount;

            if (field.length() > size)
                spaces = spacesCount - (field.length() - size);
            else if (field.length() < size)
                spaces = spacesCount + (size - field.length());

            for (int j = 0; j < spaces; j++)
                line = line.concat(" ");
        }
        return line;
    }



    public List<Message> getMessagesReceivedByUserSentByOtherUserInYearMonth(Long receiverId,
                                                                             Long senderId,
                                                                             int year,
                                                                             int month){
        return conversationService.getMessagesReceivedByUserSentByOtherUserInYearAndMonth(receiverId,
                senderId,
                year,
                month);
    }

    public void exportMessagesReceivedByUserSentByOtherUserInYearAndMonth(String fileUrl,
                                                                   Long receiverId,
                                                                   Long senderId,
                                                                   int year,
                                                                   int month) throws IOException {
        List<Message> messages = conversationService.getMessagesReceivedByUserSentByOtherUserInYearAndMonth(receiverId,
                senderId,
                year,
                month);

        PDDocument doc = new PDDocument();

        LocalDateTime yearAndMonth = LocalDateTime.of(year, month, 1, 1, 1);
        String monthString = yearAndMonth.getMonth().toString();
        String title = "Messages report from " + monthString + " " + year;
        String senderString = "Sent by: ";
        senderString += userService.findUserById(senderId).get().toString();
        String receiverString = "Received by: " + userService.findUserById(receiverId).get().toString();

        int spacesCount = 40;
        String columnNames = getColumnsLineForReport(Arrays.asList("Text", "Date"), spacesCount);


        Function<Message, String> messageToDocLine = m -> {
            var fields = Arrays.asList(m.getText(), m.getDate().format(DateTimeFormatter.ISO_DATE));
            var columnSizes = Arrays.asList(4, 4);
            return parseToDocLine(fields, columnSizes, spacesCount);
        };

        exportToPdfDocumentEntities(doc, Arrays.asList(title, senderString, receiverString),
                columnNames,
                messages,
                messageToDocLine,
                30);
        doc.save(fileUrl);
        doc.close();
    }


    private <E> void exportToPdfDocumentEntities(PDDocument document,
                                                 List<String> headerLines,
                                                 String columnNames,
                                                 List<E> entities,
                                                 Function<E, String> entityToDocumentLineFunction,
                                                 int entitiesPerPageCount) throws IOException {
        int pagesCount = 1;

        if (entities.size() > entitiesPerPageCount){
            pagesCount = entities.size() / entitiesPerPageCount;

            if (entities.size() % entitiesPerPageCount != 0)
                pagesCount++;
        }

        for(int i = 0; i < pagesCount; i++){
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(document, page);


            float startY = 750;
            stream.beginText();
            stream.setLeading(19f);
            stream.newLineAtOffset(25, 750);
            stream.setFont(PDType1Font.COURIER, 18);

            for(var line : headerLines) {
                stream.showText(line);
                stream.newLine();
                startY -= 19f;
            }
            stream.endText();

            startY -= 2 * 14.5f;

            stream.beginText();
            stream.setLeading(14.5f);
            stream.newLineAtOffset(25, startY);
            stream.setFont(PDType1Font.COURIER, 10);
            stream.showText(columnNames);
            stream.endText();
            startY -= 14.5f;
            stream.moveTo(25, startY);
            stream.lineTo(page.getCropBox().getWidth() - 25, startY);
            stream.stroke();
            startY =  startY - 14.5f;

            stream.beginText();
            stream.setLeading(20f);
            stream.newLineAtOffset(25, startY);
            stream.setFont(PDType1Font.COURIER, 10);

            for(int j = i * entitiesPerPageCount; j <  entities.size() && j < (i + 1) * entitiesPerPageCount; j++) {
                var entity = entities.get(j);
                stream.showText(entityToDocumentLineFunction.apply(entity));
                stream.newLine();
            }
            stream.endText();
            stream.close();
        }
    }

    public FriendRequestDto findFriendRequestDto(Long senderId, Long receiverId) {
        FriendRequest request = friendRequestService.findOneFriendRequest(senderId, receiverId).get();
        return new FriendRequestDto(request, userService.findUserById(senderId).get());
    }


    private List<Observer<NewConversationHasBeenCreatedEvent>> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer<NewConversationHasBeenCreatedEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<NewConversationHasBeenCreatedEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(NewConversationHasBeenCreatedEvent event) {
        observers.forEach(o -> o.update(event));
    }
}
