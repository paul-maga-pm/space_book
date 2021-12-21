package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.entities.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.service.*;
import socialnetwork.utils.containers.OrderedPair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Main {

    public static void clearDatabase(String url, String user, String password) throws SQLException{
        Connection connection = DriverManager.getConnection(url, user, password);

        connection.prepareStatement("delete from messages").executeUpdate();
        connection.prepareStatement("delete from conversation_participations").executeUpdate();
        connection.prepareStatement("delete from conversations").executeUpdate();
        connection.prepareStatement("delete from users").executeUpdate();
    }


    public static void main(String[] args) throws SQLException{
        final String url = ApplicationContext.getProperty("socialnetwork.database.url");
        final String user = ApplicationContext.getProperty("socialnetwork.database.user");
        final String password = ApplicationContext.getProperty("socialnetwork.database.password");
        clearDatabase(url, user, password);
    }
}

