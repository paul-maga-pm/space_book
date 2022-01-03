package socialnetwork.repository.database;

import socialnetwork.domain.entities.EventParticipant;
import socialnetwork.domain.entities.NotificationStatusSupplier;
import socialnetwork.utils.containers.UnorderedPair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventParticipantDatabaseRepository extends AbstractDatabaseRepository<UnorderedPair<Long, Long>, EventParticipant>{

    public EventParticipantDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public EventParticipant createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long userId = resultSet.getLong("user_id");
        Long eventId = resultSet.getLong("event_id");
        String notificationStatus = resultSet.getString("notification_status");
        return new EventParticipant(userId, eventId, NotificationStatusSupplier.getStatus(notificationStatus));
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, EventParticipant eventParticipant) throws SQLException {
        String insertSqlString = "insert into event_participants(user_id, event_id, notification_status) values (?, ?, ?)";
        PreparedStatement insertStatement = null;
        try{
            insertStatement = connection.prepareStatement(insertSqlString);
            insertStatement.setLong(1, eventParticipant.getId().first);
            insertStatement.setLong(2, eventParticipant.getId().second);
            insertStatement.setString(3, eventParticipant.getNotificationStatus().name());
            return insertStatement;
        } catch (SQLException exception){
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, UnorderedPair<Long, Long> id) throws SQLException {
        String deleteSqlString = "delete from event_participants where user_id = ? and event_id = ?";
        PreparedStatement deleteStatement = null;
        try{
            deleteStatement = connection.prepareStatement(deleteSqlString);
            deleteStatement.setLong(1, id.first);
            deleteStatement.setLong(2, id.second);
            return deleteStatement;
        } catch (SQLException exception){
            closePreparedStatement(deleteStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, UnorderedPair<Long, Long> id) throws SQLException {
        String findSqlString = "select * from event_participants where user_id = ? and event_id = ?";
        PreparedStatement findStatement = null;
        try{
            findStatement = connection.prepareStatement(findSqlString);
            findStatement.setLong(1, id.first);
            findStatement.setLong(2, id.second);
            return findStatement;
        } catch (SQLException exception){
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("select * from event_participants");
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, EventParticipant newValue) throws SQLException {
        String updateStringStatement = "UPDATE event_participants SET notification_status = ? WHERE user_id = ? AND event_id = ?";
        PreparedStatement updateStatement = null;
        try{
            updateStatement = connection.prepareStatement(updateStringStatement);
            updateStatement.setString(1, newValue.getNotificationStatus().name());
            updateStatement.setLong(2, newValue.getId().first);
            updateStatement.setLong(3, newValue.getId().second);
            return updateStatement;
        } catch (SQLException exception){
            closePreparedStatement(updateStatement);
            throw new SQLException(exception);
        }
    }
}
