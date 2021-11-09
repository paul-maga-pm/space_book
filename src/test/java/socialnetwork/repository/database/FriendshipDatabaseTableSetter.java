package socialnetwork.repository.database;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class FriendshipDatabaseTableSetter {
    static String url = ApplicationContext.getProperty("network.database.url");
    static String user = ApplicationContext.getProperty("network.database.user");
    static String password = ApplicationContext.getProperty("network.database.password");

    public static void tearDown(){
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            var deleteStatement = connection.prepareStatement("DELETE FROM friendships");
            deleteStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void setUp(List<Friendship> testData){
        tearDown();

        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            String insertStatementString = "INSERT INTO friendships(id_first_user, id_second_user) VALUES (?,?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertStatementString);
            for(Friendship friendship : testData) {
                insertStatement.setLong(1, friendship.getId().first);
                insertStatement.setLong(2, friendship.getId().second);
                insertStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
