package socialnetwork.repository.database;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.MessageDto;
import socialnetwork.exceptions.CorruptedDataException;
import socialnetwork.repository.csv.MessageDtoCSVFileRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class MessageDatabaseTableSetter {
    static String url = ApplicationContext.getProperty("network.database.url");
    static String user = ApplicationContext.getProperty("network.database.user");
    static String password = ApplicationContext.getProperty("network.database.password");

    public static void tearDown(){
        try(Connection connection = DriverManager.getConnection(url, user, password)){
            PreparedStatement deleteAllFromMessages = connection.prepareStatement("DELETE FROM messages");
            deleteAllFromMessages.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void setUp(List<MessageDto> testData){
        tearDown();

        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            String insertStringSql = "INSERT INTO messages(message_id, message, date) VALUES (?,?,?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertStringSql);

            for(var message : testData){
                insertStatement.setLong(1, message.getId());
                insertStatement.setString(2, message.getText());
                insertStatement.setTimestamp(3, Timestamp.valueOf(message.getDate()));
                insertStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
