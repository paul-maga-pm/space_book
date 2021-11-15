package socialnetwork.repository.database;

import socialnetwork.domain.models.Entity;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.DatabaseException;
import socialnetwork.repository.RepositoryInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDatabaseRepository<ID, E extends Entity<ID>>
        implements RepositoryInterface<ID, E> {
    private String url;
    private String user;
    private String password;

    public AbstractDatabaseRepository(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Creates a entity with the data of the first row from the given result set
     * @param resultSet contains fields of the entity
     * @return entity with the given data
     */
    public abstract E createEntityFromResultSet(ResultSet resultSet);

    /**
     * Creates an insert statement for the given entity on the given connection
     * @param entity entity that will be inserted
     * @param connection Connection object to a database
     * @return a PreparedStatement that represent a query that will add the entity into the database
     */
    public abstract PreparedStatement createInsertStatementForEntity(Connection connection, E entity);

    /**
     * Creates a PreparedStatement that removes the entity with the given id
     * @param id identifier of the entity that will be removed
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will remove the entity with the given id
     */
    public abstract PreparedStatement createDeleteStatementForEntityWithId(Connection connection, ID id);

    /**
     * Creates a query for selecting id, firstName and lastName of a entity
     * @param id identifier of entity that will be selected
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting the entity from database
     */
    public abstract PreparedStatement createFindStatementForEntityWithId(Connection connection, ID id);

    /**
     * Creates a query that selects all entity objects from database
     * @param connection Connection to database
     * @return a PreparedStatement object representing the query for selecting all entities from the database
     */
    public abstract PreparedStatement createSelectAllStatement(Connection connection);

    /**
     * Creates a PreparedStatement that updates the entity with the same id as newValue
     * @param newValue E object with the id of the entity in the database we want to update and the new fields values
     * @param connection Connection object to a database
     * @return PreparedStatement representing a query that will update the entity with the same id as newValue
     */
    public abstract PreparedStatement createUpdateStatementForEntity(Connection connection, E newValue);

    @Override
    public Optional<E> save(E entity) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findStatement = createFindStatementForEntityWithId(connection, entity.getId());
            ResultSet resultSet = findStatement.executeQuery();
            if(resultSet.next())
                return Optional.of(createEntityFromResultSet(resultSet));
            PreparedStatement insertStatement = createInsertStatementForEntity(connection, entity);
            insertStatement.executeUpdate();
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public List<E> getAll() {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            List<E> entities = new ArrayList<>();
            PreparedStatement selectAllStatement = createSelectAllStatement(connection);
            ResultSet resultSet = selectAllStatement.executeQuery();
            while(resultSet.next())
                entities.add(createEntityFromResultSet(resultSet));
            return entities;
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public Optional<E> findById(ID id) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findEntityStatement = createFindStatementForEntityWithId(connection, id);
            ResultSet resultSet = findEntityStatement.executeQuery();
            if(resultSet.next())
                return Optional.of(createEntityFromResultSet(resultSet));
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public Optional<E> update(E newValue) {

        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findSql = createFindStatementForEntityWithId(connection, newValue.getId());
            ResultSet resultSet = findSql.executeQuery();
            if(resultSet.next()){
                E oldValue = createEntityFromResultSet(resultSet);
                PreparedStatement updateSql = createUpdateStatementForEntity(connection, newValue);
                updateSql.executeUpdate();
                return Optional.of(oldValue);
            }
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }

    @Override
    public Optional<E> remove(ID id) {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement findEntityStatement = createFindStatementForEntityWithId(connection, id);
            ResultSet resultSet = findEntityStatement.executeQuery();
            if(resultSet.next()){
                PreparedStatement deleteStatement = createDeleteStatementForEntityWithId(connection, id);
                deleteStatement.executeUpdate();
                return Optional.of(createEntityFromResultSet(resultSet));
            }
            return Optional.empty();
        } catch (SQLException exception) {
            throw new DatabaseException(exception.getMessage());
        }
    }
}
