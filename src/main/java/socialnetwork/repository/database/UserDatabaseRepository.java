package socialnetwork.repository.database;

import socialnetwork.domain.models.User;
import socialnetwork.exceptions.DatabaseException;
import socialnetwork.repository.RepositoryInterface;

import java.sql.*;

/**
 * Repository for User model implementation on network database
 */
public class UserDatabaseRepository extends AbstractDatabaseRepository<Long, User> {

    /**
     * Creates a new database with the given connection data
     * @param url url of the database
     * @param user user of the server
     * @param password master password of the server
     */
    public UserDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }


    /**
     * Creates a query for selecting id, firstName and lastName of a user
     * @param id identifier of user that will be selected
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting the user from database
     */
    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long id){
        try{
            String findSqlString = "SELECT id, first_name, last_name FROM users WHERE id = ?";
            PreparedStatement findSql = connection.prepareStatement(findSqlString);
            findSql.setLong(1, id);
            return findSql;
        } catch (SQLException exception){
            throw new DatabaseException(exception);
        }
    }

    /**
     * Creates a query that selects all entries from the users relation
     * @param connection Connection to database
     * @return a Preparedstatement object representing the query for selecting all users from the database
     */
    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) {
        try{
            String selectAllStatementString = "SELECT * FROM users";
            return connection.prepareStatement(selectAllStatementString);
        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }

    /**
     * Creates a PreparedStatement that removes the user with the given id
     * @param id identifier of the user that will be removed
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will remove the user with the given id
     */
    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long id){
        try{
            String deleteSqlStr = "DELETE FROM users WHERE id=?";
            PreparedStatement updateSql = connection.prepareStatement(deleteSqlStr);
            updateSql.setLong(1, id);
            return updateSql;
        } catch (SQLException exception){
            throw new DatabaseException(exception);
        }
    }

    /**
     * Creates a PreparedStatement that updates the user with the same id as newValue
     * @param newValue User object with the id of the user in the database we want to update and the new firstName and
     * lastName values
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will update the user with the same id as newValue
     */
    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, User newValue){
        try{
            String updateSqlStr = "UPDATE users SET first_name=?, last_name=? WHERE id=?";
            PreparedStatement updateSql = connection.prepareStatement(updateSqlStr);
            updateSql.setString(1, newValue.getFirstName());
            updateSql.setString(2, newValue.getLastName());
            updateSql.setLong(3, newValue.getId());
            return updateSql;
        } catch (SQLException exception){
            throw new DatabaseException(exception);
        }
    }

    /**
     * Creates an insert statement for the given user on the given connection
     * @param user user that will be inserted
     * @param connection Connection object to a database
     * @return a PreparedStatement that represent a query that will add the user into the database
     */
    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, User user){
        try{
            String insertSqlStr = "INSERT INTO users(id, first_name, last_name) values (?,?,?)";
            PreparedStatement insertSql = connection.prepareStatement(insertSqlStr);
            insertSql.setLong(1, user.getId());
            insertSql.setString(2, user.getFirstName());
            insertSql.setString(3, user.getLastName());
            return insertSql;
        } catch (SQLException exception){
            throw new DatabaseException(exception);
        }
    }

    /**
     * Creates a user with the data of the first row from the given result set
     * @param resultSet contains id, firstName, lastName of the user and must point to a valid row
     * @return User with the given data
     */
    public User createEntityFromResultSet(ResultSet resultSet){
        try{
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            return new User(id, firstName, lastName);
        } catch (SQLException exception){
            throw new DatabaseException(exception);
        }
    }


}
