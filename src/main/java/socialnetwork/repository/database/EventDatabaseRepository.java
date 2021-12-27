package socialnetwork.repository.database;

import socialnetwork.domain.entities.Event;

import java.sql.*;
import java.time.LocalDate;

public class EventDatabaseRepository extends AbstractDatabaseRepository<Long, Event>{

    public EventDatabaseRepository(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public Event createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("event_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate date = resultSet.getTimestamp("date").toLocalDateTime().toLocalDate();
        String imageFile = resultSet.getString("image_file");
        return new Event(id, name, description, date, imageFile);
    }

    @Override
    public PreparedStatement createInsertStatementForEntity(Connection connection, Event event) throws SQLException {
        String insertSQLString = "insert into events(event_id, name, description, date, image_file) values (?, ?, ?, ?, ?)";
        PreparedStatement insertStatement = null;
        try{
            insertStatement = connection.prepareStatement(insertSQLString);
            insertStatement.setLong(1, event.getId());
            insertStatement.setString(2, event.getName());
            insertStatement.setString(3, event.getDescription());
            insertStatement.setDate(4, Date.valueOf(event.getDate()));
            insertStatement.setString(5, event.getImageFile());
            return insertStatement;
        }catch(SQLException exception){
            closePreparedStatement(insertStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createDeleteStatementForEntityWithId(Connection connection, Long eventId) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement createFindStatementForEntityWithId(Connection connection, Long eventId) throws SQLException {
        String findSqlString = "select * from events where event_id = ?";
        PreparedStatement findStatement = null;
        try{
            findStatement = connection.prepareStatement(findSqlString);
            findStatement.setLong(1, eventId);
            return findStatement;
        }catch (SQLException exception) {
            closePreparedStatement(findStatement);
            throw new SQLException(exception);
        }
    }

    @Override
    public PreparedStatement createSelectAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("select * from events");
    }

    @Override
    public PreparedStatement createUpdateStatementForEntity(Connection connection, Event newValue) throws SQLException {
        String updateSqlString = "update events set name = ?, description = ?, date = ?, image_file = ? where event_id = ?";
        PreparedStatement updateStatement = null;
        try{
            updateStatement = connection.prepareStatement(updateSqlString);
            updateStatement.setString(1, newValue.getName());
            updateStatement.setString(2, newValue.getDescription());
            updateStatement.setTimestamp(3, Timestamp.valueOf(String.valueOf(newValue.getDate())));
            updateStatement.setString(4, newValue.getImageFile());
            updateStatement.setLong(5, newValue.getId());
            return updateStatement;
        } catch (SQLException exception) {
            closePreparedStatement(updateStatement);
            throw new SQLException(exception);
        }

    }
}
