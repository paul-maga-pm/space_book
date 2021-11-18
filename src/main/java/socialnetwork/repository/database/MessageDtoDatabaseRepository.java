package socialnetwork.repository.database;

import socialnetwork.domain.models.MessageDto;
import socialnetwork.exceptions.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;

public class MessageDtoDatabaseRepository extends AbstractDatabaseRepository<Long, MessageDto> {
    public MessageDtoDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public MessageDto createEntityFromResultSet(ResultSet resultSet) {
        try{
            Long messageId = resultSet.getLong("message_id");
            String text = resultSet.getString("message");
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
            return new MessageDto(messageId, text, date);
        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, MessageDto message) {
        try{
            String insertSqlString = "INSERT INTO messages(message_id, message, date) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSqlString);
            insertStatement.setLong(1, message.getId());
            insertStatement.setString(2, message.getText());
            insertStatement.setTimestamp(1, Timestamp.valueOf(message.getDate()));
            return insertStatement;
        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long messageId) {
        try{
            String deleteSqlString = "DELETE FROM messages WHERE message_id=?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSqlString);
            deleteStatement.setLong(1, messageId);
            return deleteStatement;
        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long messageId) {
        try{
            String findSqlString = "SELECT (message_id, message, date) FROM messages WHERE message_id = ?";
            PreparedStatement findStatement = connection.prepareStatement(findSqlString);
            findStatement.setLong(1, messageId);
            return findStatement;
        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) {
        try{
            String selectAllSqlString = "SELECT * FROM messages";
            return connection.prepareStatement(selectAllSqlString);
        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, MessageDto newValue) {
        try{
            String updateSqlString = "UPDATE messages SET message=?, date=? WHERE message_id=?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSqlString);
            updateStatement.setString(1, newValue.getText());
            updateStatement.setTimestamp(2, Timestamp.valueOf(newValue.getDate()));
            updateStatement.setLong(3, newValue.getId());
            return updateStatement;
        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }
}
