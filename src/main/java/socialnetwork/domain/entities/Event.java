package socialnetwork.domain.entities;

import java.time.LocalDate;
import java.util.Objects;

public class Event extends Entity<Long>{
    private String name;
    private String description;
    private LocalDate date;
    private String imageFile;

    public Event(Long eventId, String name, String description, LocalDate date, String imageFile) {
        super(eventId);
        this.name = name;
        this.description = description;
        this.date = date;
        this.imageFile = imageFile;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getImageFile() {
        return imageFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        if (!super.equals(o)) return false;
        Event that = (Event) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description)
                && Objects.equals(date, that.date) && Objects.equals(imageFile, that.imageFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description, date, imageFile);
    }
}
