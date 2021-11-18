package socialnetwork.repository.database;

import socialnetwork.domain.models.ReplyDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReplyDtoDatabaseRepository
        extends AbstractDatabaseRepository<Long, ReplyDto> {


    public ReplyDtoDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public ReplyDto createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        try{
            Long replyId = resultSet.getLong("reply_id");
            Long messageId = resultSet.getLong("message_id");
            return new ReplyDto(replyId, messageId);
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, ReplyDto replyDto) throws SQLException {
        String insertSqlString = "INSERT INTO replies(reply_id, message_id) VALUES (?,?)";
        PreparedStatement insertStatement = null;

        try{
           insertStatement = connection.prepareStatement(insertSqlString);
           insertStatement.setLong(1, replyDto.getId());
           insertStatement.setLong(2, replyDto.getIdOfMessageThatIsRepliedTo());
           return insertStatement;
        } catch (SQLException exception) {
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long idOfReply) throws SQLException {
        String deleteSqlString = "DELETE FROM replies WHERE reply_id = ?";
        PreparedStatement deleteStatement = null;
        try{
            deleteStatement = connection.prepareStatement(deleteSqlString);
            deleteStatement.setLong(1, idOfReply);
            return deleteStatement;
        } catch (SQLException exception) {
            closePreparedStatement(deleteStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long idOfReply) throws SQLException {
        String findSqlString = "SELECT message_id, reply_id " +
                "FROM replies " +
                "WHERE reply_id = ?";
        PreparedStatement findStatement = null;
        try{
            findStatement = connection.prepareStatement(findSqlString);
            findStatement.setLong(1, idOfReply);
            return findStatement;
        } catch (SQLException exception) {
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        try{
            return connection.prepareStatement("SELECT * FROM replies");
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, ReplyDto newValue) throws SQLException {
        String updateSqlString = "UPDATE replies " +
                "SET message_id = ? " +
                "WHERE reply_id = ?";
        PreparedStatement updateStatement = null;

        try{
            updateStatement = connection.prepareStatement(updateSqlString);
            updateStatement.setLong(1, newValue.getIdOfMessageThatIsRepliedTo());
            updateStatement.setLong(2, newValue.getId());
            return updateStatement;
        } catch (SQLException exception) {
            closePreparedStatement(updateStatement);
            throw new SQLException(exception);
        }
    }
}
