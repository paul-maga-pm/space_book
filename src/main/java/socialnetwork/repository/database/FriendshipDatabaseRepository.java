package socialnetwork.repository.database;

import socialnetwork.domain.entities.Friendship;
import socialnetwork.utils.containers.UnorderedPair;

import java.sql.*;
import java.time.LocalDateTime;

public class FriendshipDatabaseRepository
        extends AbstractDatabaseRepository<UnorderedPair<Long, Long>, Friendship> {

    /**
     * Creates a new database with the given connection data
     * @param url url of the database
     * @param user user of the server
     * @param password master password of the server
     */
    public FriendshipDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    /**
     * Creates a new friendship object with the data of the first row of resultSet
     * @param resultSet contains friendship data in format (id_first_user, id_second_user)
     * @return a Friendship with the given data
     */
    @Override
    public Friendship createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        try{
            Long id1 = resultSet.getLong("id_first_user");
            Long id2 = resultSet.getLong("id_second_user");
            LocalDateTime date = resultSet.getTimestamp("friendship_date").toLocalDateTime();
            return new Friendship(id1, id2, date);
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a query for inserting a new friendship
     * @param friendship friendship that will be added
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for inserting the friendship into database
     */
    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, Friendship friendship) throws SQLException {
        String insertStringStatement = "INSERT INTO friendships(id_first_user, id_second_user, friendship_date) VALUES (?,?,?)";
        PreparedStatement insertStatement = null;
        try {
            insertStatement = connection.prepareStatement(insertStringStatement);
            insertStatement.setLong(1, friendship.getId().first);
            insertStatement.setLong(2, friendship.getId().second);
            insertStatement.setTimestamp(3, Timestamp.valueOf(friendship.getDate()));
            return insertStatement;
        } catch (SQLException e) {
            closePreparedStatement(insertStatement);
            throw new SQLException(e);
        }
    }

    /**
     * Creates a query for removing a friendship
     * @param id identifier of friendship that will be removed
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for removing the friendship from database
     */
    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, UnorderedPair<Long, Long> id) throws SQLException {
        String deleteStringStatement = "DELETE FROM friendships WHERE id_first_user=? AND id_second_user=? OR " +
                "id_second_user=? AND id_first_user=?";
        PreparedStatement deleteStatement = null;
        try{
            deleteStatement = connection.prepareStatement(deleteStringStatement);
            deleteStatement.setLong(1, id.first);
            deleteStatement.setLong(2, id.second);
            deleteStatement.setLong(3, id.first);
            deleteStatement.setLong(4, id.second);
            return deleteStatement;
        } catch (SQLException exception){
            closePreparedStatement(deleteStatement);
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a query for selecting a friendship
     * @param id identifier of friendship that will be selected
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting the friendship from database
     */
    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, UnorderedPair<Long, Long> id) throws SQLException {
        String findFriendshipByIdStatementString = "SELECT * FROM friendships WHERE id_first_user=? " +
                "AND id_second_user=? OR id_second_user=? AND id_first_user=?";
        PreparedStatement findStatement = null;
        try {
            findStatement = connection.prepareStatement(findFriendshipByIdStatementString);
            findStatement.setLong(1, id.first);
            findStatement.setLong(2, id.second);
            findStatement.setLong(3, id.first);
            findStatement.setLong(4, id.second);
            return findStatement;
        } catch (SQLException e) {
            closePreparedStatement(findStatement);
            throw new SQLException(e);
        }
    }

    /**
     * Creates a query that selects all friendships from database
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting all friendships from the database
     */
    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        try{
            return connection.prepareStatement("SELECT * FROM friendships");
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a PreparedStatement that updates the friendship with the same id as newValue
     * @param newValue E object with the id of the friendship in the database we want to update and the new fields values
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will update the friendship with the same id as newValue
     */
    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, Friendship newValue) throws SQLException {
        String updateSqlStr = "UPDATE friendships SET friendship_date=? WHERE id_first_user=? AND id_second_user=? OR " +
                "id_second_user=? AND id_first_user=?";
        PreparedStatement updateSql = null;
        try{
            updateSql = connection.prepareStatement(updateSqlStr);
            updateSql.setTimestamp(1, Timestamp.valueOf(newValue.getDate()));
            updateSql.setLong(2, newValue.getId().first);
            updateSql.setLong(3, newValue.getId().second);
            updateSql.setLong(4, newValue.getId().first);
            updateSql.setLong(5, newValue.getId().second);
            return updateSql;
        } catch (SQLException exception){
            closePreparedStatement(updateSql);
            throw new SQLException(exception);
        }
    }
}
