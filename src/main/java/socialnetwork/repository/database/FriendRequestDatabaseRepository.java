package socialnetwork.repository.database;

import socialnetwork.domain.models.FriendRequest;
import socialnetwork.domain.models.StatusSupplier;
import socialnetwork.utils.containers.UnorderedPair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class FriendRequestDatabaseRepository extends
        AbstractDatabaseRepository<UnorderedPair<Long, Long>, FriendRequest>{

    /**
     * Creates a new database with the given connection data
     * @param url url of the database
     * @param user user of the server
     * @param password master password of the server
     */
    public FriendRequestDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    /**
     * Creates a new friendRequest object with the data of the first row of resultSet
     * @param resultSet contains friendRequest data in format (id_first_user, id_second_user, status)
     * @return a FriendRequest with the given data
     */
    @Override
    public FriendRequest createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        try{
            Long idOfFirstUser = resultSet.getLong("id_first_user");
            Long idOfSecondUser = resultSet.getLong("id_second_user");
            String status = resultSet.getString("status");
            return new FriendRequest(idOfFirstUser, idOfSecondUser, StatusSupplier.getStatus(status));
        } catch (SQLException exception){
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a query for inserting a new friendRequest
     * @param friendRequest friendRequest that will be added
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for inserting the friendRequest into database
     */
    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, FriendRequest friendRequest) throws SQLException {
        String insertStringStatement = "INSERT INTO friend_requests (id_first_user, id_second_user, status) VALUES (?, ?, ?)";
        PreparedStatement insertStatement = null;
        try{
            insertStatement = connection.prepareStatement(insertStringStatement);
            insertStatement.setLong(1, friendRequest.getId().first);
            insertStatement.setLong(2, friendRequest.getId().second);
            insertStatement.setString(3, friendRequest.getStatus().name());
            return insertStatement;
        } catch (SQLException exception){
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a query for removing a friendRequest
     * @param id identifier of friendRequest that will be removed
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for removing the friendRequest from database
     */
    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, UnorderedPair<Long, Long> id) throws SQLException {
        String deleteStringStatement = "DELETE FROM friend_requests WHERE id_first_user = ? AND id_second_user = ? OR " +
                "id_first_user = ? AND id_second_user = ?";
        PreparedStatement deleteStatement = null;
        try{
            deleteStatement = connection.prepareStatement(deleteStringStatement);
            deleteStatement.setLong(1, id.first);
            deleteStatement.setLong(2, id.second);
            deleteStatement.setLong(3, id.second);
            deleteStatement.setLong(4, id.first);
            return deleteStatement;
        } catch (SQLException exception){
            closePreparedStatement(deleteStatement);
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a query for selecting a friendRequest
     * @param id identifier of friendRequest that will be selected
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting the friendRequest from database
     */
    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, UnorderedPair<Long, Long> id) throws SQLException {
        String findFriendRequestByIdStringStatement = "SELECT * FROM friend_requests WHERE id_first_user = ? AND id_second_user = ? OR " +
                "id_first_user = ? AND id_second_user = ?";
        PreparedStatement findFriendRequestByIdStatement = null;
        try{
            findFriendRequestByIdStatement = connection.prepareStatement(findFriendRequestByIdStringStatement);
            findFriendRequestByIdStatement.setLong(1, id.first);
            findFriendRequestByIdStatement.setLong(2, id.second);
            findFriendRequestByIdStatement.setLong(3, id.second);
            findFriendRequestByIdStatement.setLong(4, id.first);
            return findFriendRequestByIdStatement;
        } catch (SQLException exception){
            closePreparedStatement(findFriendRequestByIdStatement);
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a query that selects all friendRequests from database
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting all friendRequests from the database
     */
    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        try{
            return connection.prepareStatement("SELECT * FROM friend_requests");
        } catch (SQLException exception){
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a PreparedStatement that updates the friendRequest with the same id as newFriendRequest
     * @param newFriendRequest E object with the id of the friendRequest in the database we want to update and the new field values
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will update the friendRequest with the same id as newFriendRequest
     */
    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, FriendRequest newFriendRequest) throws SQLException {
        String updateFriendRequestStringStatement = "UPDATE friend_requests SET status = ? WHERE " +
                "id_first_user = ? AND id_second_user = ? OR id_first_user = ? AND id_second_user = ?";
        PreparedStatement updateFriendRequestStatement = null;
        try{
            updateFriendRequestStatement = connection.prepareStatement(updateFriendRequestStringStatement);
            updateFriendRequestStatement.setString(1, newFriendRequest.getStatus().name());
            updateFriendRequestStatement.setLong(2, newFriendRequest.getId().first);
            updateFriendRequestStatement.setLong(3, newFriendRequest.getId().second);
            updateFriendRequestStatement.setLong(4, newFriendRequest.getId().second);
            updateFriendRequestStatement.setLong(5, newFriendRequest.getId().first);
            return updateFriendRequestStatement;
        } catch (SQLException exception){
            closePreparedStatement(updateFriendRequestStatement);
            throw new SQLException(exception);
        }
    }
}
