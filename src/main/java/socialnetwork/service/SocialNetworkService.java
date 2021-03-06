package socialnetwork.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import socialnetwork.domain.entities.*;
import socialnetwork.events.NewConversationHasBeenCreatedEvent;
import socialnetwork.exceptions.EntityNotFoundValidationException;

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

    /**
     * Constructor of the service
     */
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

    /**
     * Sets the number of user per page of the filtration by name
     */
    public void setNumberOfUserPerFiltrationByNamePage(int usersCount){
        userService.setUsersPerPageCount(usersCount);
    }

    /**
     * Sets current page of the user filtration by name
     */
    public void setCurrentPageIndexOfUserFiltration(int pageIndex){
        userService.setCurrentPageIndex(pageIndex);
    }

    /**
     * Returns all users that have in their name the given string, from the page
     */
    public List<User> getUsersByName(String name, int pageIndex){
        return userService.getUsersByName(name, pageIndex);
    }


    /**
     * Returns the number of users that have in their name the given string
     */
    public int getNumberOfUsersThatHaveInTheirNameTheString(String name) {
        return  userService.getNumberOfUsersThatHaveInTheirNameTheString(name);
    }

    /**
     * Adds a new user and returns it
     * @param profilePictureFile relative path to the Resource folder to the profile picture
     */
    public User signUpUserService(String firstName, String lastName, String userName, String password, String profilePictureFile){
        User signedUser = userService.signUpUser(firstName, lastName, userName, password, profilePictureFile);
        signedUser.setUserName(userName);
        return signedUser;
    }

    /**
     * Returns the user with the given credentials
     * @throws EntityNotFoundValidationException if the user doesn't exist
     */
    public User loginUserService(String userName, String password){
        return userService.loginUser(userName, password);
    }

    /**
     * Updates the given user and returns the old value
     */
    public Optional<User> updateUserService(User newUser){
        return userService.updateUser(newUser);
    }

    /**
     * Sends a friend request from user with id senderId to user with id receiverId
     */
    public void sendFriendRequestService(Long senderId, Long receiverId){
        friendRequestService.sendFriendRequestService(senderId, receiverId);
    }

    /**
     * Updates the status of the friend request between the users to the given status
     */
    public Optional<FriendRequest> acceptOrRejectFriendRequestService(Long firstUserId, Long secondUserId, Status status){
        return friendRequestService.acceptOrRejectFriendRequestService(firstUserId, secondUserId, status);
    }

    /**
     * Removes the friendship between the users and returns the old value
     */
    public Optional<Friendship> removeFriendshipService(Long firstUserId, Long secondUserId){
        Optional<Friendship> existingFriendshipOptional = networkService.removeFriendshipService(firstUserId, secondUserId);
        if(existingFriendshipOptional.isPresent())
            friendRequestService.rejectADeletedFriendship(existingFriendshipOptional.get().getId().first,
                    existingFriendshipOptional.get().getId().second);
        return existingFriendshipOptional;
    }

    /**
     * Removes the friend request send by the user with id senderId to user with id receiverId
     */
    public Optional<FriendRequest> withdrawFriendRequest(Long senderId, Long receiverId){
        return friendRequestService.removeFriendRequestService(senderId, receiverId);
    }

    /**
     * Returns the friend request sent from user with id senderId to user with id receiverID
     */
    public Optional<FriendRequest> findOneFriendRequestService(Long senderId, Long receiverId){
        return friendRequestService.findOneFriendRequest(senderId, receiverId);
    }

    /**
     * Returns all friend requests received by user
     */
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

    /**
     * Returns all friend request sent by user
     */
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

    /**
     * Returns all friends of the user
     */
    public List<FriendshipDto> findAllFriendsOfUser(Long userId){
        return networkService.findAllFriendsForUserService(userId);
    }


    /**
     * Creates a new conversation with the given name, description and participants and returns it
     * @param loggedUserId id of the user that creates the conversation
     * @param conversationName name of the conversation
     * @param conversationDescription description of the conversation
     * @param participantsIdWithoutLoggedUser list of the id-s of the participants of the conversation
     * @return ConversationDto that contains the name, description and the list of participants (User entities)
     */
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

    /**
     * Sends a message in the conversation with the given id
     * @param conversationId id of the conversation in which the message is sent
     * @param senderId id of the User that sends the message
     * @param text body of the message
     * @param date the date when the message was sent
     */
    public void sendMessageInConversation(Long senderId, Long conversationId, String text, LocalDateTime date){
        conversationService.sendMessageInConversation(conversationId, senderId, text, date);
    }

    /**
     * Returns a list with all conversations of the user with the given id
     */
    public LocalDateTime findDateOfFriendship(Long firstUserId, Long secondUserId){
        return networkService.findDateOfFriendship(firstUserId, secondUserId);
    }

    /**
     * Returns a list with all conversations of the user with the given id
     */
    public List<ConversationDto> getConversationsOfUser(Long userId){
        return conversationService.getConversationsOfUser(userId);
    }

    /**
     * Returns the number of friend request accepted by the user
     */
    public int countAcceptedFriendRequestsSentByUser(Long senderId) {
        int count = 0;

        for(var friendRequest : friendRequestService.getAllFriendRequestsSentByUser(senderId))
            if (friendRequest.getStatus() == Status.APPROVED)
                count ++;
        return count;
    }

    /**
     * Returns the number of friend request received by the user
     */
    public int countFriendRequestsReceivedByUser(Long receiverId) {
        return friendRequestService.getAllFriendRequestsReceivedByUser(receiverId).size();
    }

    /**
     * Creates a new Event and returns it
     * @param name name of the event
     * @param description description of the event
     * @param date date when the event takes place
     * @param imageFile relative path to the Resource folder of the project to the picture of the event
     */
    public Event addEventService(String name, String description, LocalDate date, String imageFile){
        return eventService.addEvent(name, description, date, imageFile);
    }

    /**
     * Returns a list with all events
     */
    public List<Event> getAllEventsService(){
        return eventService.getAllEvents();
    }

    /**
     * Returns the event participation of the given user from the given event
     */
    public Optional<EventParticipant> findOneEventParticipantService(Long userId, Long eventId){
        return eventService.findOneEventParticipant(userId, eventId);
    }

    /**
     * Creates a new event participation with the given status and returns it
     * @param userId id of the participant
     * @param eventId id of the event
     * @param notificationStatus status of the notification with two possible values: SUBSCRIBED, UNSUBSCRIBED
     */
    public EventParticipant addEventParticipantService(Long userId, Long eventId, NotificationStatus notificationStatus){
        return eventService.addEventParticipant(userId, eventId, notificationStatus);
    }

    /**
     * Removes an event participation for the given user from the given event and returns the old value
     */
    public Optional<EventParticipant> removeEventParticipantService(Long userId, Long eventId){
        return eventService.removeEventParticipant(userId, eventId);
    }

    /**
     * Updates the given event participation and returns the old one
     */
    public Optional<EventParticipant> updateEventParticipantService(EventParticipant newEventParticipant){
        return eventService.updateEventParticipant(newEventParticipant);
    }

    /**
     * Returns all events that the user is subscribed to and are taking place in the next 5 days
     */
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

    /**
     * Returns a list with all messages received by the user in the given year and month
     */
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


    /**
     * Returns a list with all FriendshipDto-s from the given year and month for the given user
     */
    public List<FriendshipDto> getAllNewFriendshipsOfUserFromYearAndMonth(Long userId, int year, int month){
        return networkService.getAllNewFriendshipsOfUserFromYearAndMonth(userId, year, month);
    }


    /**
     * Exports the activity of the user to the location given by the fileUrl
     */
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

    /**
     * Returns a string that contains the columns of the report with the given number of spaces between them
     */
    private String getColumnsLineForReport(List<String> columnNamesList, int spacesBetweenColumns){
        String columnNames = columnNamesList.get(0);

        for(int j = 1; j < columnNamesList.size(); j++) {
            for (int i = 0; i < spacesBetweenColumns; i++)
                columnNames = columnNames.concat(" ");

            columnNames = columnNames.concat(columnNamesList.get(j));
        }
        return columnNames;
    }


    /**
     * Returns a string that contains the given fields formatted so that the columns above the rows of the lines of
     * the report will be aligned with the fields of each column
     */
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


    /**
     * Returns all messages between the users sent in the given year and month
     */
    public List<Message> getMessagesReceivedByUserSentByOtherUserInYearMonth(Long receiverId,
                                                                             Long senderId,
                                                                             int year,
                                                                             int month){
        return conversationService.getMessagesReceivedByUserSentByOtherUserInYearAndMonth(receiverId,
                senderId,
                year,
                month);
    }

    /**
     * Exports the messages between users to the location given by the fileUrl
     */
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


    /**
     * Exports the entities to the pdf file at the given location
     * @param document pdfdoc where the entities will be exported
     * @param headerLines each string of this list represents a row in the header
     * @param columnNames list of the names of the columns of the report
     * @param entities list of entities that will be exported
     * @param entityToDocumentLineFunction parser function (from entity to string)
     * @param entitiesPerPageCount number of entities that will appear on one page
     * @throws IOException
     */
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

    /**
     * Returns the friend request sent
     */
    public FriendRequestDto findFriendRequestDto(Long senderId, Long receiverId) {
        FriendRequest request = friendRequestService.findOneFriendRequest(senderId, receiverId).get();
        return new FriendRequestDto(request, userService.findUserById(senderId).get());
    }


    /**
     * Observer implementation for creating new conversations
     */
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
