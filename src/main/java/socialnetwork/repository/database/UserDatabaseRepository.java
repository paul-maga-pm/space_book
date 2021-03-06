package socialnetwork.repository.database;

import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.DatabaseException;
import socialnetwork.repository.paging.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository for User model implementation on network database
 */
public class UserDatabaseRepository
        extends AbstractDatabaseRepository<Long, User>
        implements PagingUserRepository {

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
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long id) throws SQLException {
        String findSqlString = "SELECT * FROM users u  " +
                "inner join user_credentials u_c on u_c.user_id = u.id" +
                " WHERE id = ?";
        PreparedStatement findSql = null;
        try{
            findSql = connection.prepareStatement(findSqlString);
            findSql.setLong(1, id);
            return findSql;
        } catch (SQLException exception){
            closePreparedStatement(findSql);
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a query that selects all entries from the users relation
     * @param connection Connection to database
     * @return a Preparedstatement object representing the query for selecting all users from the database
     */
    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        try{
            String selectAllStatementString = "SELECT * FROM users u  " +
                    "inner join user_credentials u_c on u_c.user_id = u.id";
            return connection.prepareStatement(selectAllStatementString);
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a PreparedStatement that removes the user with the given id
     * @param id identifier of the user that will be removed
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will remove the user with the given id
     */
    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long id) throws SQLException {
        String deleteSqlStr = "DELETE FROM users WHERE id=?";
        PreparedStatement deleteSql = null;
        try{
            deleteSql = connection.prepareStatement(deleteSqlStr);
            deleteSql.setLong(1, id);
            return deleteSql;
        } catch (SQLException exception){
            closePreparedStatement(deleteSql);
            throw new SQLException(exception);
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
    public PreparedStatement createUpdateStatementForEntity(Connection connection, User newValue) throws SQLException {
        String updateSqlStr = "UPDATE users SET first_name=?, last_name=?, profile_picture_file=? WHERE id=?";
        PreparedStatement updateSql = null;
        try{
            updateSql = connection.prepareStatement(updateSqlStr);
            updateSql.setString(1, newValue.getFirstName());
            updateSql.setString(2, newValue.getLastName());
            updateSql.setString(3, newValue.getProfilePictureFile());
            updateSql.setLong(4, newValue.getId());
            return updateSql;
        } catch (SQLException exception){
            closePreparedStatement(updateSql);
            throw new SQLException(exception);
        }
    }

    /**
     * Creates an insert statement for the given user on the given connection
     * @param user user that will be inserted
     * @param connection Connection object to a database
     * @return a PreparedStatement that represent a query that will add the user into the database
     */
    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, User user) throws SQLException {
        String insertSqlStr = "INSERT INTO users(id, first_name, last_name, profile_picture_file) values (?,?,?,?)";
        PreparedStatement insertSql = null;
        try{
            insertSql = connection.prepareStatement(insertSqlStr);
            insertSql.setLong(1, user.getId());
            insertSql.setString(2, user.getFirstName());
            insertSql.setString(3, user.getLastName());
            insertSql.setString(4, user.getProfilePictureFile());
            return insertSql;
        } catch (SQLException exception){
            closePreparedStatement(insertSql);
            throw new SQLException(exception);
        }
    }

    /**
     * Creates a user with the data of the first row from the given result set
     * @param resultSet contains id, firstName, lastName of the user and must point to a valid row
     * @return User with the given data
     */
    public User createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        try{
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String userName = resultSet.getString("username");
            String profilePictureFile = resultSet.getString("profile_picture_file");
            User user = new User(id, firstName, lastName, profilePictureFile);
            user.setUserName(userName);
            return user;
        } catch (SQLException exception){
            throw new SQLException(exception);
        }
    }


    @Override
    public PageInterface<User> findAllUsersByName(PageableInterface pageable, String name) {
        String filterSqlString =
                "select id, first_name, last_name, username, profile_picture_file from users " +
                        "inner join user_credentials on users.id = user_credentials.user_id " +
                "where LOWER(first_name)  || ' ' || LOWER(last_name) like '%' || ? || '%'";
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement filterStatement = connection.prepareStatement(filterSqlString)){

            filterStatement.setString(1, name);
            List<User> allUsers = new ArrayList<>();
            ResultSet resultSet = filterStatement.executeQuery();
            while(resultSet.next()){
                User user = createEntityFromResultSet(resultSet);
                allUsers.add(user);
            }

            Paginator<User> paginator = new Paginator<>(pageable, allUsers);
            resultSet.close();
            return paginator.paginate();

        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }

    @Override
    public int countUsersThatHaveInTheirNameTheString(String name) {
        String filterSqlString =
                "select count(*) as total from users " +
                "where LOWER(first_name)  || ' ' || LOWER(last_name) like '%' || ? || '%'";
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement filterStatement = connection.prepareStatement(filterSqlString)){

            filterStatement.setString(1, name);
            ResultSet resultSet = filterStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("total");
        } catch (SQLException exception) {
            throw new DatabaseException(exception);
        }
    }
}
