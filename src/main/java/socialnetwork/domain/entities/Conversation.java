package socialnetwork.domain.entities;

import java.util.Objects;

public class Conversation extends Entity<Long>{
    private String name;
    private String description;

    public Conversation(Long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation)) return false;
        if (!super.equals(o)) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description);
    }
}
