package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.repository.csv.UserCSVFileRepository;

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
        connection.prepareStatement("delete from users").executeUpdate();

        String userFilePath = ApplicationContext.getProperty("socialnetwork.csv.users");
        UserCSVFileRepository userRepository = new UserCSVFileRepository(userFilePath);

        PreparedStatement insertStatementIntoUsers =
                connection.prepareStatement("insert into users(id, first_name, last_name) values (?,?,?)");

        PreparedStatement insertStatementIntoUserCredentials =
                connection.prepareStatement("insert into user_credentials(user_id, username, password) values (?, ?, ?)");

        for(var u : userRepository.getAll()){
            insertStatementIntoUsers.setLong(1, u.getId());
            insertStatementIntoUsers.setString(2, u.getFirstName());
            insertStatementIntoUsers.setString(3, u.getLastName());
            insertStatementIntoUsers.executeUpdate();

            String firstName = u.getFirstName();
            String lastName = u.getLastName();
            String email = firstName.toLowerCase(Locale.ROOT) + "." + lastName.toLowerCase(Locale.ROOT) + "@gmail.com";
            insertStatementIntoUserCredentials.setLong(1, u.getId());
            insertStatementIntoUserCredentials.setString(2, email);
            insertStatementIntoUserCredentials.setString(3, "parola");

            insertStatementIntoUserCredentials.executeUpdate();
        }

    }


    public static void main(String[] args) throws SQLException{
        final String url = ApplicationContext.getProperty("socialnetwork.database.url");
        final String user = ApplicationContext.getProperty("socialnetwork.database.user");
        final String password = ApplicationContext.getProperty("socialnetwork.database.password");
        loadToDatabase(url, user, password);
    }
}

