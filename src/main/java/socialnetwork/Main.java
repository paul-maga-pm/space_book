package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.*;
import socialnetwork.repository.csv.FriendshipCSVFileRepository;
import socialnetwork.repository.csv.UserCSVFileRepository;
import socialnetwork.repository.database.*;

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


    }
}

