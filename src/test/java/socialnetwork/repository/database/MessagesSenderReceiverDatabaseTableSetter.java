package socialnetwork.repository.database;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.MessageSenderReceiverDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MessagesSenderReceiverDatabaseTableSetter {
    static String url = ApplicationContext.getProperty("network.database.url");
    static String user = ApplicationContext.getProperty("network.database.user");
    static String password = ApplicationContext.getProperty("network.database.password");

    public static void tearDown(){
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM messages_sender_receiver")){
            deleteStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void setUp(List<MessageSenderReceiverDto> testData){
        tearDown();
        String insertSqlString = "INSERT INTO messages_sender_receiver(message_id, sender_id, receiver_id) VALUES (?,?,?)";
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement insertStatement = connection.prepareStatement(insertSqlString)) {

            for(var messageSenderReceiver : testData){
                insertStatement.setLong(1, messageSenderReceiver.getId().getMessageId());
                insertStatement.setLong(2, messageSenderReceiver.getId().getSenderId());
                insertStatement.setLong(3, messageSenderReceiver.getId().getReceiverId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
