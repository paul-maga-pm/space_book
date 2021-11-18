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
    public MessageDto createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        try{
            Long messageId = resultSet.getLong("message_id");
            String text = resultSet.getString("message");
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
            return new MessageDto(messageId, text, date);
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, MessageDto message) throws SQLException {
        String insertSqlString = "INSERT INTO messages(message_id, message, date) VALUES (?, ?, ?)";
        PreparedStatement insertStatement = null;
        try{
            insertStatement = connection.prepareStatement(insertSqlString);
            insertStatement.setLong(1, message.getId());
            insertStatement.setString(2, message.getText());
            insertStatement.setTimestamp(3, Timestamp.valueOf(message.getDate()));
            return insertStatement;
        } catch (SQLException exception) {
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long messageId) throws SQLException {
        String deleteSqlString = "DELETE FROM messages WHERE message_id=?";
        PreparedStatement deleteStatement = null;
        try{
            deleteStatement = connection.prepareStatement(deleteSqlString);
            deleteStatement.setLong(1, messageId);
            return deleteStatement;
        } catch (SQLException exception) {
            closePreparedStatement(deleteStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long messageId) throws SQLException {
        String findSqlString = "SELECT message_id, message, date FROM messages WHERE message_id = ?";
        PreparedStatement findStatement = null;
        try{
            findStatement = connection.prepareStatement(findSqlString);
            findStatement.setLong(1, messageId);
            return findStatement;
        } catch (SQLException exception) {
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        try{
            String selectAllSqlString = "SELECT * FROM messages";
            return connection.prepareStatement(selectAllSqlString);
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, MessageDto newValue) throws SQLException {
        String updateSqlString = "UPDATE messages SET message=?, date=? WHERE message_id=?";
        PreparedStatement updateStatement = null;
        try{
            updateStatement = connection.prepareStatement(updateSqlString);
            updateStatement.setString(1, newValue.getText());
            updateStatement.setTimestamp(2, Timestamp.valueOf(newValue.getDate()));
            updateStatement.setLong(3, newValue.getId());
            return updateStatement;
        } catch (SQLException exception) {
            closePreparedStatement(updateStatement);
            throw new SQLException(exception);
        }
    }
}
