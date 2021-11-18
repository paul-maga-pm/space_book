package socialnetwork.repository.database;

import socialnetwork.domain.models.MessageSenderReceiverDto;
import socialnetwork.domain.models.MessageSenderReceiverDtoId;
import socialnetwork.exceptions.DatabaseException;

import java.sql.*;

public class MessageSenderReceiverDtoDatabaseRepository
        extends AbstractDatabaseRepository<MessageSenderReceiverDtoId, MessageSenderReceiverDto> {

    private Connection connectionToDatabase = null;

    public MessageSenderReceiverDtoDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public MessageSenderReceiverDto createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        try{
            Long senderId = resultSet.getLong("sender_id");
            Long receiverId = resultSet.getLong("receiver_id");
            Long messageId = resultSet.getLong("message_id");
            return new MessageSenderReceiverDto(messageId, senderId, receiverId);
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }

    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection,
                                                            MessageSenderReceiverDto messageSenderReceiver)
                                                            throws SQLException {
        String insertSqlString = "INSERT INTO messages_sender_receiver(sender_id, receiver_id, message_id) VALUES (?, ?, ?)";
        PreparedStatement insertStatement = null;
        try{
            insertStatement = connection.prepareStatement(insertSqlString);
            insertStatement.setLong(1, messageSenderReceiver.getId().getSenderId());
            insertStatement.setLong(2, messageSenderReceiver.getId().getReceiverId());
            insertStatement.setLong(3, messageSenderReceiver.getId().getMessageId());
            return insertStatement;
        } catch (SQLException exception) {
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection,
                                                                  MessageSenderReceiverDtoId messageSenderReceiverDtoId)
                                                                  throws SQLException {
        String deleteSqlString = "DELETE FROM messages_sender_receiver " +
                "WHERE message_id = ? and sender_id = ? and receiver_id = ?";
        PreparedStatement deleteStatement = null;
        try{
            deleteStatement = connection.prepareStatement(deleteSqlString);
            deleteStatement.setLong(1, messageSenderReceiverDtoId.getMessageId());
            deleteStatement.setLong(2, messageSenderReceiverDtoId.getSenderId());
            deleteStatement.setLong(3, messageSenderReceiverDtoId.getReceiverId());
            return deleteStatement;
        } catch(SQLException exception){
            closePreparedStatement(deleteStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection,
                                                                MessageSenderReceiverDtoId messageSenderReceiverDtoId)
                                                                throws SQLException {
        String findSqlString = "SELECT message_id, sender_id, receiver_id FROM messages_sender_receiver " +
                "WHERE message_id = ? AND sender_id = ? AND receiver_id = ?";
        PreparedStatement findStatement = null;

        try{
            findStatement = connection.prepareStatement(findSqlString);
            findStatement.setLong(1, messageSenderReceiverDtoId.getMessageId());
            findStatement.setLong(2, messageSenderReceiverDtoId.getSenderId());
            findStatement.setLong(3, messageSenderReceiverDtoId.getReceiverId());
            return findStatement;
        } catch (SQLException exception) {
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        try{
            String selectAllSqlString = "SELECT * FROM messages_sender_receiver";
            return connection.prepareStatement(selectAllSqlString);
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection,
                                                            MessageSenderReceiverDto newValue)
                                                            throws SQLException {
        String updateSqlString = "UPDATE messages_sender_receiver " +
                "SET message_id = ?, sender_id=?, receiver_id=? " +
                "WHERE message_id = ? AND sender_id = ? AND receiver_id = ?";
        PreparedStatement updateStatement = null;

        try{
            updateStatement = connection.prepareStatement(updateSqlString);
            updateStatement.setLong(1, newValue.getId().getMessageId());
            updateStatement.setLong(2, newValue.getId().getSenderId());
            updateStatement.setLong(3, newValue.getId().getReceiverId());

            updateStatement.setLong(4, newValue.getId().getMessageId());
            updateStatement.setLong(5, newValue.getId().getSenderId());
            updateStatement.setLong(6, newValue.getId().getReceiverId());

            return updateStatement;
        } catch (SQLException exception) {
            closePreparedStatement(updateStatement);
            throw new SQLException(exception);
        }
    }
}
