package socialnetwork.models;

import javafx.scene.Node;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class NotificationModel  {
    public LocalDateTime getDate() {
        return date;
    }

    private LocalDateTime date;

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public abstract Node getViewForModel();

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
