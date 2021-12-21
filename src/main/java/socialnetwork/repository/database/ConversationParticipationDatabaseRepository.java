package socialnetwork.repository.database;

import socialnetwork.domain.entities.ConversationParticipation;
import socialnetwork.domain.entities.ConversationParticipationId;
import socialnetwork.exceptions.UnimplemetedException;
import socialnetwork.utils.containers.OrderedPair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConversationParticipationDatabaseRepository extends
        AbstractDatabaseRepository<ConversationParticipationId, ConversationParticipation> {
    public ConversationParticipationDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public ConversationParticipation createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long conversationId = resultSet.getLong("conversation_id");
        Long userId = resultSet.getLong("user_id");
        return new ConversationParticipation(userId, conversationId);
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, ConversationParticipation entity)
            throws SQLException {

        String insertStr = "insert into conversation_participations(conversation_id, user_id) values (?, ?)";
        PreparedStatement insertStatement = null;

        try{
            insertStatement = connection.prepareStatement(insertStr);
            insertStatement.setLong(1, entity.getConversationId());
            insertStatement.setLong(2, entity.getParticipantId());
            return insertStatement;
        } catch (SQLException exception) {
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, ConversationParticipationId id)
            throws SQLException {
        throw new UnimplemetedException();
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, ConversationParticipationId id)
            throws SQLException {

        String findStr = "select * from conversation_participations where conversation_id = ? and user_id = ?";
        PreparedStatement findStatement = null;

        try{
            findStatement = connection.prepareStatement(findStr);
            findStatement.setLong(1, id.getConversationId());
            findStatement.setLong(2, id.getUserId());
            return findStatement;
        } catch (SQLException exception){
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("select * from conversation_participations");
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, ConversationParticipation newValue)
            throws SQLException {
        throw new UnimplemetedException();
    }
}
