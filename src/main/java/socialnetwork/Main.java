package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.*;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.csv.FriendshipCSVFileRepository;
import socialnetwork.repository.csv.UserCSVFileRepository;
import socialnetwork.repository.database.*;
import socialnetwork.service.AdminService;
import socialnetwork.service.FriendRequestService;
import socialnetwork.service.NetworkService;
import socialnetwork.service.SocialNetworkAdminService;
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
            PreparedStatement deleteFriendRequests = connection.prepareStatement("DELETE  FROM friend_requests");
            ){
            deleteReplies.executeUpdate();
            deleteSenderReceivers.executeUpdate();
            deleteMessages.executeUpdate();
            deleteFriendshipsStatement.executeUpdate();
            deleteUsersStatement.executeUpdate();
            deleteFriendRequests.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        UserDatabaseRepository userDb = new UserDatabaseRepository(url, user, password);
        FriendshipDatabaseRepository friendshipDb = new FriendshipDatabaseRepository(url, user, password);

        for(User u : userRepository.getAll())
            userDb.save(u);
    }


    public static void main(String[] args) {
        final String url = ApplicationContext.getProperty("socialnetwork.database.url");
        final String user = ApplicationContext.getProperty("socialnetwork.database.user");
        final String password = ApplicationContext.getProperty("socialnetwork.database.password");
        loadToDatabase(url, user, password);

        RepositoryInterface<Long, User> userRepo = new UserDatabaseRepository(url, user, password);
        RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepo =
                new FriendshipDatabaseRepository(url, user, password);
        RepositoryInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepo =
                new FriendRequestDatabaseRepository(url, user, password);

        EntityValidatorInterface<Long, User> userVal = new UserValidator();
        EntityValidatorInterface<UnorderedPair<Long, Long>, Friendship> friendshipVal = new FriendshipValidator(userRepo);
        EntityValidatorInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestVal = new FriendRequestValidator(userRepo);

        AdminService adminService = new AdminService(userRepo, userVal);
        NetworkService networkService = new NetworkService(friendshipRepo, userRepo, friendshipVal);
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepo,
                friendshipRepo,
                friendRequestVal);

        SocialNetworkAdminService socialNetworkService = new SocialNetworkAdminService(adminService,
                networkService,
                null,
                friendRequestService);

        ConsoleApplicationInterface ui = new ConsoleApplicationInterface(socialNetworkService);
        ui.run();
    }
}

