package socialnetwork.repository.database;

import socialnetwork.domain.entities.Message;
import socialnetwork.exceptions.UnimplemetedException;

import java.sql.*;
import java.time.LocalDateTime;

public class MessageDatabaseRepository extends AbstractDatabaseRepository<Long, Message> {
    public MessageDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public Message createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long conversationId = resultSet.getLong("conversation_id");
        Long senderId = resultSet.getLong("sender_id");
        String text = resultSet.getString("text");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        Long id = resultSet.getLong("id");
        return new Message(id, conversationId, senderId, text, date);
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, Message message) throws SQLException {
        String insertSql = "insert into messages(id," +
                "conversation_id," +
                "sender_id," +
                "text," +
                "date) values (?, ?, ?, ?, ?)";
        PreparedStatement insertStatement = null;

        try{
           insertStatement = connection.prepareStatement(insertSql);
           insertStatement.setLong(1, message.getId());
           insertStatement.setLong(2, message.getConversationId());
           insertStatement.setLong(3, message.getSenderId());
           insertStatement.setString(4, message.getText());
           insertStatement.setTimestamp(5, Timestamp.valueOf(message.getDate()));

           return insertStatement;
        } catch (SQLException exception){
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long aLong) throws SQLException {
        throw new UnimplemetedException();
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long id) throws SQLException {
        String findStr = "select * from messages where id = ?";
        PreparedStatement findStatement = null;

        try{
            findStatement = connection.prepareStatement(findStr);
            findStatement.setLong(1, id);
            return findStatement;
        } catch (SQLException exception){
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("select * from messages");
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, Message newValue) throws SQLException {
        throw new UnimplemetedException();
    }
}
