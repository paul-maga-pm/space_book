package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.controllers.NetworkController;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.domain.validators.*;
import socialnetwork.exceptions.CorruptedDataException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.csv.FriendshipCSVFileRepository;
import socialnetwork.repository.csv.UserCSVFileRepository;
import socialnetwork.repository.database.FriendshipDatabaseRepository;
import socialnetwork.repository.database.UserDatabaseRepository;
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

        try(Connection connection = DriverManager.getConnection(url, user, password)){
            PreparedStatement deleteFriendshipsStatement = connection.prepareStatement("DELETE FROM friendships");
            PreparedStatement deleteUsersStatement = connection.prepareStatement("DELETE  FROM users");
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

        UserService userService = new UserService(userRepository, friendshipRepository, userValidator);
        NetworkService networkService = new NetworkService(friendshipRepository, userRepository, friendshipValidator);
        NetworkController networkController = new NetworkController(userService, networkService);
        ConsoleApplicationInterface ui = new ConsoleApplicationInterface(networkController);
        ui.run();
    }
}

