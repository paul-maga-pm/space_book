package socialnetwork.service;

import socialnetwork.domain.entities.Event;
import socialnetwork.domain.entities.EventParticipant;
import socialnetwork.domain.entities.NotificationStatus;
import socialnetwork.domain.validators.EntityValidator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EventService {
    private EntityValidator<Long, Event> eventValidator;
    private Repository<Long, Event> eventRepository;
    private EntityValidator<UnorderedPair<Long, Long>, EventParticipant> eventParticipantValidator;
    private Repository<UnorderedPair<Long, Long>, EventParticipant> eventParticipantRepository;

    /**
     * Constructor of the service
     */
    public EventService(EntityValidator<Long, Event> eventValidator,
                        Repository<Long, Event> eventRepository,
                        EntityValidator<UnorderedPair<Long, Long>, EventParticipant> eventParticipantValidator,
                        Repository<UnorderedPair<Long, Long>, EventParticipant> eventParticipantRepository) {
        this.eventValidator = eventValidator;
        this.eventRepository = eventRepository;
        this.eventParticipantValidator = eventParticipantValidator;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    /**
     * Creates a new Event and returns it
     * @param name name of the event
     * @param description description of the event
     * @param date date when the event takes place
     * @param imageFile relative path to the Resource folder of the project to the picture of the event
     */
    public Event addEvent(String name, String description, LocalDate date, String imageFile){
        Long eventId = findAvailableId();
        Event event = new Event(eventId, name, description, date, imageFile);
        eventValidator.validate(event);
        eventRepository.save(event);
        return event;
    }


    /**
     * Returns a list with all events
     */
    public List<Event> getAllEvents(){
        return eventRepository.getAll();
    }

    /**
     * Creates a new event participation with the given status and returns it
     * @param userId id of the participant
     * @param eventId id of the event
     * @param notificationStatus status of the notification with two possible values: SUBSCRIBED, UNSUBSCRIBED
     */
    public EventParticipant addEventParticipant(Long userId, Long eventId, NotificationStatus notificationStatus){
        EventParticipant eventParticipant = new EventParticipant(userId, eventId, notificationStatus);
        eventParticipantValidator.validate(eventParticipant);
        eventParticipantRepository.save(eventParticipant);
        return eventParticipant;
    }

    /**
     * Removes an event participation for the given user from the given event and returns the old value
     */
    public Optional<EventParticipant> removeEventParticipant(Long userId, Long eventId){
        return eventParticipantRepository.remove(new UnorderedPair<>(userId, eventId));
    }

    /**
     * Updates the given event participation and returns the old one
     */
    public Optional<EventParticipant> updateEventParticipant(EventParticipant newEventParticipant){
        return eventParticipantRepository.update(newEventParticipant);
    }

    /**
     * Returns the event participation of the given user from the given event
     */
    public Optional<EventParticipant> findOneEventParticipant(Long userId, Long eventId){
        return eventParticipantRepository.findById(new UnorderedPair<>(userId, eventId));
    }

    /**
     * Returns a long representing an available id for creating a new event
     */
    private Long findAvailableId() {
        var optional = eventRepository.getAll().stream()
                .max(Comparator.comparing(Event::getId));
        if(optional.isEmpty())
            return 1L;
        return optional.get().getId() + 1;

    }
}
