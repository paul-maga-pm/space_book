package socialnetwork.repository.database;

import socialnetwork.domain.models.Friendship;
import socialnetwork.exceptions.DatabaseException;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDatabaseRepository
        implements RepositoryInterface<UnorderedPair<Long, Long>, Friendship> {

    private String url;
    private String user;
    private String password;
    private static final String FIND_FRIENDSHIP_BY_ID_SQL_STRING = "SELECT * FROM friendships WHERE id_first_user=? " +
            "AND id_second_user=? OR id_second_user=? AND id_first_user=?";

    /**
     * Creates a new database with the given connection data
     * @param url url of the database
     * @param user user of the server
     * @param password master password of the server
     */
    public FriendshipDatabaseRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<Friendship> save(Friendship friendship) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findStatement = createFindStatementForFriendshipId(friendship.getId(), connection);
            ResultSet resultSet = findStatement.executeQuery();
            if(resultSet.next())
                return Optional.of(createFriendshipFromResultSet(resultSet));
            PreparedStatement insertStatement = createInsertStatementForFriendship(friendship, connection);
            insertStatement.executeUpdate();
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public List<Friendship> getAll() {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            List<Friendship> friendships = new ArrayList<>();
            PreparedStatement selectAllStatement = connection.prepareStatement("SELECT * FROM friendships");
            ResultSet resultSet = selectAllStatement.executeQuery();
            while(resultSet.next())
                friendships.add(createFriendshipFromResultSet(resultSet));
            return friendships;
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public Optional<Friendship> findById(UnorderedPair<Long, Long> id) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findFriendshipStatement = createFindStatementForFriendshipId(id,connection);
            ResultSet resultSet = findFriendshipStatement.executeQuery();
            if(resultSet.next())
                return Optional.of(createFriendshipFromResultSet(resultSet));
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public Optional<Friendship> update(Friendship newValue) {
        return findById(newValue.getId());
    }

    @Override
    public Optional<Friendship> remove(UnorderedPair<Long, Long> id) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findFriendshipStatement = createFindStatementForFriendshipId(id, connection);
            ResultSet resultSet = findFriendshipStatement.executeQuery();
            if(resultSet.next()){
                PreparedStatement deleteStatement = createDeleteStatementForFriendship(id, connection);
                deleteStatement.executeUpdate();
                return Optional.of(createFriendshipFromResultSet(resultSet));
            }
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    /**
     * Creates a new friendship object with the data of the first row of resultSet
     * @param resultSet contains friendship data in format (id_first_user, id_second_user)
     * @return a Friendship with the given data
     */
    private Friendship createFriendshipFromResultSet(ResultSet resultSet){
        try{
            Long id1 = resultSet.getLong("id_first_user");
            Long id2 = resultSet.getLong("id_second_user");
            return new Friendship(id1, id2);
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    /**
     * Creates a query for inserting a new friendship
     * @param friendship friendship that will be added
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for inserting the friendship into database
     */
    private PreparedStatement createInsertStatementForFriendship(Friendship friendship, Connection connection){
        try {
            String insertStringStatement = "INSERT INTO friendships(id_first_user, id_second_user) VALUES (?,?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertStringStatement);
            insertStatement.setLong(1, friendship.getId().first);
            insertStatement.setLong(2, friendship.getId().second);
            return insertStatement;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Creates a query for removing a friendship
     * @param id identifier of friendship that will be removed
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for removing the friendship from database
     */
    private PreparedStatement createDeleteStatementForFriendship(UnorderedPair<Long, Long> id, Connection connection){
        try{
            String deleteStringStatement = "DELETE FROM friendships WHERE id_first_user=? AND id_second_user=? OR " +
                    "id_second_user=? AND id_first_user=?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteStringStatement);
            deleteStatement.setLong(1, id.first);
            deleteStatement.setLong(2, id.second);
            deleteStatement.setLong(3, id.first);
            deleteStatement.setLong(4, id.second);
            return deleteStatement;
        } catch (SQLException exception){
            throw new DatabaseException(exception.getMessage());
        }
    }

    /**
     * Creates a query for selecting a friendship
     * @param id identifier of friendship that will be selected
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting the friendship from database
     */
    private PreparedStatement createFindStatementForFriendshipId(UnorderedPair<Long, Long> id, Connection connection){
        try {
            PreparedStatement findStatement = connection.prepareStatement(FIND_FRIENDSHIP_BY_ID_SQL_STRING);
            findStatement.setLong(1, id.first);
            findStatement.setLong(2, id.second);
            findStatement.setLong(3, id.first);
            findStatement.setLong(4, id.second);
            return findStatement;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
