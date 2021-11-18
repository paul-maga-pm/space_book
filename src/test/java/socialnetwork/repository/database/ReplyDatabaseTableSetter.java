package socialnetwork.repository.database;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.ReplyDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ReplyDatabaseTableSetter {
    static String url = ApplicationContext.getProperty("network.database.url");
    static String user = ApplicationContext.getProperty("network.database.user");
    static String password = ApplicationContext.getProperty("network.database.password");

    public static void tearDown(){
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement deleteAllStatement = connection.prepareStatement("DELETE FROM replies")){
            deleteAllStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void setUp(List<ReplyDto> replies){
        tearDown();

        String insertSqlString = "INSERT INTO replies(reply_id, message_id) VALUES (?, ?)";
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement insertStatement = connection.prepareStatement(insertSqlString)){

           for(var reply : replies){
               insertStatement.setLong(1, reply.getId());
               insertStatement.setLong(2, reply.getIdOfMessageThatIsRepliedTo());
               insertStatement.executeUpdate();
           }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
