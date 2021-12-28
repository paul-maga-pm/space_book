package socialnetwork.models;

import javafx.scene.Node;
import javafx.scene.control.Label;
import socialnetwork.domain.entities.Event;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EventModel extends NotificationModel{
    private Event event;

    public EventModel(Event event) {
        this.event = event;
    }

    @Override
    public Node getViewForModel() {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), event.getDate());
        if (days == 0)
            return new Label("Today is the event " + event.getName() + " !");
        if (days == 1)
            return new Label("There is only one day until " + event.getName() + " !");
        return new Label("There are " + days + " days until " + event.getName() + " !");
    }
}
