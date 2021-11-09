package socialnetwork.repository.database;

import socialnetwork.domain.models.User;
import socialnetwork.exceptions.DatabaseException;
import socialnetwork.repository.RepositoryInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for User model implementation on network database
 */
public class UserDatabaseRepository implements RepositoryInterface<Long, User> {
    private String url;
    private String user;
    private String password;

    /**
     * Creates a new database with the given connection data
     * @param url url of the database
     * @param user user of the server
     * @param password master password of the server
     */
    public UserDatabaseRepository(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<User> save(User user) {
        try(Connection connection = DriverManager.getConnection(url, this.user, password)) {
            PreparedStatement findSql = createFindUserByIdStatement(user.getId(), connection);
            ResultSet resultSet = findSql.executeQuery();
            if(resultSet.next())
                return Optional.of(createUserFromResultSet(resultSet));
            else{
                PreparedStatement insertSql = createInsertStatementForUser(user, connection);
                insertSql.executeUpdate();
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            List<User> users = new ArrayList<>();
            PreparedStatement selectSql = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = selectSql.executeQuery();
            while(resultSet.next())
                users.add(createUserFromResultSet(resultSet));
            return users;
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findSql = createFindUserByIdStatement(id, connection);
            ResultSet resultSet = findSql.executeQuery();
            if(resultSet.next())
                return Optional.of(createUserFromResultSet(resultSet));
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public Optional<User> update(User newValue) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findSql = createFindUserByIdStatement(newValue.getId(), connection);
            ResultSet resultSet = findSql.executeQuery();
            if(resultSet.next()){
                User oldValue = createUserFromResultSet(resultSet);
                PreparedStatement updateSql = createUpdateStatementForUser(newValue, connection);
                updateSql.executeUpdate();
                return Optional.of(oldValue);
            }
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public Optional<User> remove(Long id) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findSql = createFindUserByIdStatement(id, connection);
            ResultSet resultSet = findSql.executeQuery();
            if(resultSet.next()){
                User oldValue = createUserFromResultSet(resultSet);
                PreparedStatement updateSql = createDeleteStatementForUser(id, connection);
                updateSql.executeUpdate();
                return Optional.of(oldValue);
            }
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    /**
     * Creates a query for id, firstName and lastName of a user
     * @param id identifier of user that will be selected
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting the friendship from database
     */
    private PreparedStatement createFindUserByIdStatement(Long id, Connection connection){
        try{
            String findSqlString = "SELECT id, first_name, last_name FROM users WHERE id = ?";
            PreparedStatement findSql = connection.prepareStatement(findSqlString);
            findSql.setLong(1, id);
            return findSql;
        } catch (SQLException exception){
            throw new DatabaseException(exception.getMessage());
        }
    }

    /**
     * Creates a PreparedStatement that removes the user with the given id
     * @param id identifier of the user that will be removed
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will remove the user with the given id
     */
    private PreparedStatement createDeleteStatementForUser(Long id, Connection connection){
        try{
            String deleteSqlStr = "DELETE FROM users WHERE id=?";
            PreparedStatement updateSql = connection.prepareStatement(deleteSqlStr);
            updateSql.setLong(1, id);
            return updateSql;
        } catch (SQLException exception){
            throw new DatabaseException(exception.getMessage());
        }
    }

    /**
     * Creates a PreparedStatement that updates the user with the same id as newValue
     * @param newValue User object with the id of the user in the database we want to update and the new firstName and
     * lastName values
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will update the user with the same id as newValue
     */
    private PreparedStatement createUpdateStatementForUser(User newValue, Connection connection){
        try{
            String updateSqlStr = "UPDATE users SET first_name=?, last_name=? WHERE id=?";
            PreparedStatement updateSql = connection.prepareStatement(updateSqlStr);
            updateSql.setString(1, newValue.getFirstName());
            updateSql.setString(2, newValue.getLastName());
            updateSql.setLong(3, newValue.getId());
            return updateSql;
        } catch (SQLException exception){
            throw new DatabaseException(exception.getMessage());
        }
    }

    /**
     * Creates an insert statement for the given user on the given connection
     * @param user user that will be inserted
     * @param connection Connection object to a database
     * @return a PreparedStatement that represent a query that will add the user into the database
     */
    private PreparedStatement createInsertStatementForUser(User user, Connection connection){
        try{
            String insertSqlStr = "INSERT INTO users(id, first_name, last_name) values (?,?,?)";
            PreparedStatement insertSql = connection.prepareStatement(insertSqlStr);
            insertSql.setLong(1, user.getId());
            insertSql.setString(2, user.getFirstName());
            insertSql.setString(3, user.getLastName());
            return insertSql;
        } catch (SQLException exception){
            throw new DatabaseException(exception.getMessage());
        }
    }

    /**
     * Creates a user with the data of the first row from the given result set
     * @param resultSet contains id, firstName, lastName of the user and must point to a valid row
     * @return User with the given data
     */
    private User createUserFromResultSet(ResultSet resultSet){
        try{
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            return new User(id, firstName, lastName);
        } catch (SQLException exception){
            throw new DatabaseException(exception.getMessage());
        }
    }
}
