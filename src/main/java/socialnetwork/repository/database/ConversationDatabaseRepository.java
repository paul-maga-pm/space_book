package socialnetwork.repository.database;

import socialnetwork.domain.entities.Conversation;
import socialnetwork.exceptions.UnimplemetedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConversationDatabaseRepository extends AbstractDatabaseRepository<Long, Conversation> {
    public ConversationDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public Conversation createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        return new Conversation(id, name, description);
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, Conversation conversation) throws SQLException {
        String insertStr = "insert into conversations(id, name, description) values (?,?,?)";
        PreparedStatement insertStatement = null;

        try{
            insertStatement = connection.prepareStatement(insertStr);
            insertStatement.setLong(1, conversation.getId());
            insertStatement.setString(2, conversation.getName());
            insertStatement.setString(3, conversation.getDescription());
            return insertStatement;
        } catch (SQLException exception){
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long aLong) throws SQLException {
        throw new UnimplemetedException();
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long id) throws SQLException {
        String findStr = "select * from conversations where id = ?";
        PreparedStatement findStatement = null;

        try{
            findStatement = connection.prepareStatement(findStr);
            findStatement.setLong(1, id);
            return findStatement;
        } catch (SQLException exception){
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }

    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("select * from conversations");
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, Conversation newValue) throws SQLException {
        throw new UnimplemetedException();
    }
}
