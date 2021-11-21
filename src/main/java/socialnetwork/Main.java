package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.controllers.SocialNetworkController;
import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.*;
import socialnetwork.exceptions.CorruptedDataException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.csv.FriendshipCSVFileRepository;
import socialnetwork.repository.csv.UserCSVFileRepository;
import socialnetwork.repository.database.*;
import socialnetwork.service.ConversationService;
import socialnetwork.service.FriendRequestService;
import socialnetwork.service.NetworkService;
import socialnetwork.service.UserService;
import socialnetwork.ui.ConsoleApplicationInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {

    public static void loadToDatabase(String url, String user, String password){
        UserCSVFileRepository userRepository = new UserCSVFileRepository(
                ApplicationContext.getProperty("socialnetwork.csv.users"));
        FriendshipCSVFileRepository friendshipRepository = new FriendshipCSVFileRepository(
                ApplicationContext.getProperty("socialnetwork.csv.friendships")
        );

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement deleteFriendshipsStatement = connection.prepareStatement("DELETE FROM friendships");
            PreparedStatement deleteUsersStatement = connection.prepareStatement("DELETE  FROM users");
            PreparedStatement deleteMessages = connection.prepareStatement("DELETE FROM messages");
            PreparedStatement deleteSenderReceivers = connection.prepareStatement("DELETE FROM messages_sender_receiver");
            PreparedStatement deleteReplies = connection.prepareStatement("DELETE FROM replies");
            ){
            deleteReplies.executeUpdate();
            deleteSenderReceivers.executeUpdate();
            deleteMessages.executeUpdate();
            deleteFriendshipsStatement.executeUpdate();
            deleteUsersStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        UserDatabaseRepository userDb = new UserDatabaseRepository(url, user, password);
        FriendshipDatabaseRepository friendshipDb = new FriendshipDatabaseRepository(url, user, password);

        for(User u : userRepository.getAll())
            userDb.save(u);

        for(Friendship f : friendshipRepository.getAll())
            friendshipDb.save(f);
    }


    public static void main(String[] args) {

        String url = ApplicationContext.getProperty("socialnetwork.database.url");
        String user = ApplicationContext.getProperty("socialnetwork.database.user");
        String password = ApplicationContext.getProperty("socialnetwork.database.password");
        //loadToDatabase(url, user, password);
        RepositoryInterface<Long, User> userRepository = new UserDatabaseRepository(url, user,password);
        EntityValidatorInterface<Long, User> userValidator = new UserValidator();

        try {
            var userDataSetValidator = new EntityDataSetValidator<>(userValidator, userRepository);
            userDataSetValidator.validateDataSet("Network data is corrupted.\nNetwork contains users that" +
                    "are not valid");
        } catch (CorruptedDataException exception){
            System.out.println(exception.getMessage());
            return;
        }

        RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepository = new FriendshipDatabaseRepository(url, user, password);
        EntityValidatorInterface<UnorderedPair<Long, Long>, Friendship> friendshipValidator = new FriendshipValidator(userRepository);
        try{
            var friendshipDataSetValidator = new EntityDataSetValidator<>(friendshipValidator, friendshipRepository);
            friendshipDataSetValidator.validateDataSet("Network data is corrupted.\nNetwork contains " +
                    "friendships between users that don't exist.");
        } catch (CorruptedDataException exception){
            System.out.println(exception.getMessage());
            return;
        }

        RepositoryInterface<Long, MessageDto> messageDtoRepository = new MessageDtoDatabaseRepository(url, user, password);
        RepositoryInterface<MessageSenderReceiverDtoId, MessageSenderReceiverDto> messageSenderReceiverDtoRepository = new MessageSenderReceiverDtoDatabaseRepository(url, user, password);
        RepositoryInterface<Long, ReplyDto> replyDtoRepository = new ReplyDtoDatabaseRepository(url, user, password);
        EntityValidatorInterface<Long, Message> messageValidator = new MessageValidator(userRepository);

        RepositoryInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepository = new FriendRequestDatabaseRepository(url, user, password);
        EntityValidatorInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestValidator = new FriendRequestValidator(userRepository);

        UserService userService = new UserService(userRepository, userValidator);
        NetworkService networkService = new NetworkService(friendshipRepository, userRepository, friendshipValidator);
        ConversationService conversationService = new ConversationService(messageDtoRepository, messageSenderReceiverDtoRepository, replyDtoRepository, userRepository, messageValidator);
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepository, friendshipRepository, friendRequestValidator);
        SocialNetworkController socialNetworkController = new SocialNetworkController(userService, networkService, conversationService, friendRequestService);
        ConsoleApplicationInterface ui = new ConsoleApplicationInterface(socialNetworkController);
        ui.run();
    }
}

