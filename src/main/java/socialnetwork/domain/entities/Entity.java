package socialnetwork.domain.entities;


import java.util.Objects;


/**
 * Base class for every model used in the application
 * @param <ID> type of the identifier
 */
public class Entity <ID>{
    private ID id;

    /**
     * Constructor that creates a new Entity with the given id
     * @param id identifier of the object
     */
    public Entity(ID id) {
        this.id = id;
    }

    /**
     * Getter method for identifier
     * @return identifier of the entity
     */
    public ID getId() {
        return id;
    }

    /**
     * Setter method for identifier
     * @param id new value of identifier
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Checks if this and o are equal by value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity<?> entity)) return false;
        return Objects.equals(id, entity.id);
    }

    /**
     * Returns hashCode of this Entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Parses this object into String format
     * @return a string in format "ID: {identifier of this Entity}"
     */
    @Override
    public String toString() {
        return "ID: " + id;
    }
}
