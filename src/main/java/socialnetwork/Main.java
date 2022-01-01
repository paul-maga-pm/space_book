package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.entities.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.csv.UserCSVFileRepository;
import socialnetwork.repository.database.*;
import socialnetwork.service.*;
import socialnetwork.utils.containers.UnorderedPair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

public class Main {

    public static void loadToDatabase(String url, String user, String password) throws SQLException{
        Connection connection = DriverManager.getConnection(url, user, password);

        connection.prepareStatement("delete from messages").executeUpdate();
        connection.prepareStatement("delete from conversation_participations").executeUpdate();
        connection.prepareStatement("delete from conversations").executeUpdate();
        connection.prepareStatement("delete from friendships").executeUpdate();
        connection.prepareStatement("delete from friend_requests").executeUpdate();
        connection.prepareStatement("delete from user_credentials").executeUpdate();
        connection.prepareStatement("delete from event_participants").executeUpdate();
        connection.prepareStatement("delete from events").executeUpdate();
        connection.prepareStatement("delete from users").executeUpdate();

        String userFilePath = ApplicationContext.getProperty("socialnetwork.csv.users");
        UserCSVFileRepository userRepository = new UserCSVFileRepository(userFilePath);

        PreparedStatement insertStatementIntoUsers =
                connection.prepareStatement("insert into users(id, first_name, last_name) values (?,?,?)");

        PreparedStatement insertStatementIntoUserCredentials =
                connection.prepareStatement("insert into user_credentials(user_id, username, password) values (?, ?, ?)");

        var service = createService();
        int i = 0;
        for(var u : userRepository.getAll()){
            String firstName = u.getFirstName();
            String lastName = u.getLastName();
            String email = firstName.toLowerCase(Locale.ROOT) + "." + lastName.toLowerCase(Locale.ROOT) + "@gmail.com";
            service.signUpUserService(firstName, lastName, email, "parola");
        }

    }

    public static SocialNetworkService createService(){

        final String url = ApplicationContext.getProperty("socialnetwork.database.url");
        final String user = ApplicationContext.getProperty("socialnetwork.database.user");
        final String password = ApplicationContext.getProperty("socialnetwork.database.password");

        Repository<Long, User> userRepo = new UserDatabaseRepository(url, user, password);
        Repository<UnorderedPair<Long, Long>, Friendship> friendshipRepo =
                new FriendshipDatabaseRepository(url, user, password);
        Repository<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepo =
                new FriendRequestDatabaseRepository(url, user, password);
        Repository<Long, UserCredential> credentialRepo = new UserCredentialDatabaseRepository(url, user, password);
        Repository<Long, Event> eventRepository = new EventDatabaseRepository(url, user, password);
        Repository<UnorderedPair<Long, Long>, EventParticipant> eventParticipantRepository = new EventParticipantDatabaseRepository(url, user, password);

        EntityValidator<Long, User> userVal = new UserValidator();
        EntityValidator<UnorderedPair<Long, Long>, Friendship> friendshipVal = new FriendshipValidator(userRepo);
        EntityValidator<UnorderedPair<Long, Long>, FriendRequest> friendRequestVal = new FriendRequestValidator(userRepo);
        EntityValidator<Long, UserCredential> signUpCredentialVal = new UserSignUpCredentialValidator(credentialRepo);
        EntityValidator<Long, Event> eventValidator = new EventValidator();
        EntityValidator<UnorderedPair<Long, Long>, EventParticipant> eventParticipantValidator = new EventParticipantValidator(userRepo, eventRepository);

        UserService userService = new UserService(userRepo, credentialRepo, signUpCredentialVal, userVal);
        NetworkService networkService = new NetworkService(friendshipRepo, userRepo, friendshipVal);
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepo, friendshipRepo, friendRequestVal);
        EventService eventService = new EventService(eventValidator, eventRepository, eventParticipantValidator, eventParticipantRepository);

        ConversationDatabaseRepository conversationRepo = new ConversationDatabaseRepository(url, user, password);
        MessageDatabaseRepository messageRepo = new MessageDatabaseRepository(url, user, password);
        ConversationParticipationDatabaseRepository participationRepo =
                new ConversationParticipationDatabaseRepository(url, user, password);
        ConversationService conversationService = new ConversationService(userRepo,
                conversationRepo,
                participationRepo,
                messageRepo,
                new MessageValidator(participationRepo),
                new ConversationValidator(),
                new ConversationParticipationValidator(userRepo, conversationRepo));
        return new SocialNetworkService(userService,
                networkService,
                friendRequestService,
                conversationService,
                eventService);
    }


    public static void main(String[] args) throws SQLException{
        final String url = ApplicationContext.getProperty("socialnetwork.database.url");
        final String user = ApplicationContext.getProperty("socialnetwork.database.user");
        final String password = ApplicationContext.getProperty("socialnetwork.database.password");
        loadToDatabase(url, user, password);
//        var service = createService();
//        try {
//            service.exportNewFriendsAndNewMessagesOfUserFromMonth("report.pdf",
//                    13L,
//                    12);
//
//            service.exportMessagesReceivedByUserSentByOtherUserInMonth("messages.pdf",
//                    13L,
//                    7L,
//                    12);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}

