package socialnetwork.controllers;

import javafx.application.Platform;
import javafx.scene.control.Label;
import socialnetwork.Run;
import socialnetwork.domain.entities.Event;
import socialnetwork.domain.entities.User;
import socialnetwork.service.SocialNetworkService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class NotificationChecker implements Runnable {
    private SocialNetworkService service;
    private User loggedUser;
    private List<Event> eventsUserParticipatesAt = new ArrayList<>();

    public NotificationChecker(SocialNetworkService service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000L);
                var actualEventsUserParticipatesAt
                        = service.getAllEventsThatAreCloseToCurrentDateForUser(loggedUser.getId());

                for (var event : actualEventsUserParticipatesAt)
                    if (!eventsUserParticipatesAt.contains(event)) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                String notificationMessage;
                                long days = ChronoUnit.DAYS.between(LocalDate.now(), event.getDate());
                                if (days == 0)
                                    notificationMessage = "Today is the event " + event.getName() + " !";
                                else if (days == 1)
                                    notificationMessage = "There is only one day until " + event.getName() + " !";
                                else
                                    notificationMessage = "There are " + days + " days until " + event.getName() + " !";

                                Run.showPopUpWindow("Upcoming Event", notificationMessage);
                            }
                        });
                    }
                this.eventsUserParticipatesAt = actualEventsUserParticipatesAt;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
