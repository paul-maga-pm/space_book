package socialnetwork.repository.database;

import socialnetwork.domain.models.UserCredential;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCredentialDatabaseRepository extends AbstractDatabaseRepository<Long, UserCredential> {
    public UserCredentialDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public UserCredential createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("user_id");
        String userName = resultSet.getString("userName");
        String password = resultSet.getString("password");
        return new UserCredential(id, userName, password);
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, UserCredential userCredential) throws SQLException {
        String insertSqlString = "insert into user_credentials(user_id, userName, password) values (?, ?, ?)";
        PreparedStatement insertStatement = null;

        try{
            insertStatement = connection.prepareStatement(insertSqlString);
            insertStatement.setLong(1, userCredential.getId());
            insertStatement.setString(2, userCredential.getUserName());
            insertStatement.setString(3, userCredential.getPassword());
            return insertStatement;
        } catch (SQLException exception) {
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long userId) throws SQLException {
        String deleteSqlString = "delete from user_credentials where user_id = ?";
        PreparedStatement deleteStatement = null;

        try{
            deleteStatement = connection.prepareStatement(deleteSqlString);
            deleteStatement.setLong(1, userId);
            return deleteStatement;
        } catch (SQLException exception) {
            closePreparedStatement(deleteStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long userId) throws SQLException {
        String findSqlString = "select user_id, userName, password from user_credentials where user_id = ?";
        PreparedStatement findStatement = null;

        try{
            findStatement = connection.prepareStatement(findSqlString);
            findStatement.setLong(1, userId);
            return findStatement;
        } catch (SQLException exception) {
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("select * from user_credentials");
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, UserCredential newValue) throws SQLException {
        String updateSqlString = "update user_credentials set userName = ?, password = ? where user_id = ?";
        PreparedStatement updateStatement = null;

        try{
            updateStatement = connection.prepareStatement(updateSqlString);
            updateStatement.setString(1, newValue.getUserName());
            updateStatement.setString(2, newValue.getPassword());
            updateStatement.setLong(3, newValue.getId());
            return updateStatement;
        } catch (SQLException exception) {
            closePreparedStatement(updateStatement);
            throw new SQLException(exception);
        }
    }
}
