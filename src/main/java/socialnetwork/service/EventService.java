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

    public EventService(EntityValidator<Long, Event> eventValidator,
                        Repository<Long, Event> eventRepository,
                        EntityValidator<UnorderedPair<Long, Long>, EventParticipant> eventParticipantValidator,
                        Repository<UnorderedPair<Long, Long>, EventParticipant> eventParticipantRepository) {
        this.eventValidator = eventValidator;
        this.eventRepository = eventRepository;
        this.eventParticipantValidator = eventParticipantValidator;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    public Event addEvent(String name, String description, LocalDate date, String imageFile){
        Long eventId = findAvailableId();
        Event event = new Event(eventId, name, description, date, imageFile);
        eventValidator.validate(event);
        eventRepository.save(event);
        return event;
    }

    public Optional<Event> findOneEvent(Long eventId){
        return eventRepository.findById(eventId);
    }

    public List<Event> getAllEvents(){
        return eventRepository.getAll();
    }

    public EventParticipant addEventParticipant(Long userId, Long eventId, NotificationStatus notificationStatus){
        EventParticipant eventParticipant = new EventParticipant(userId, eventId, notificationStatus);
        eventParticipantValidator.validate(eventParticipant);
        eventParticipantRepository.save(eventParticipant);
        return eventParticipant;
    }

    public Optional<EventParticipant> removeEventParticipant(Long userId, Long eventId){
        return eventParticipantRepository.remove(new UnorderedPair<>(userId, eventId));
    }

    public Optional<EventParticipant> findOneEventParticipant(Long userId, Long eventId){
        return eventParticipantRepository.findById(new UnorderedPair<>(userId, eventId));
    }

    public List<EventParticipant> getAllEventParticipants(){
        return eventParticipantRepository.getAll();
    }

    private Long findAvailableId() {
        var optional = eventRepository.getAll().stream()
                .max(Comparator.comparing(Event::getId));
        if(optional.isEmpty())
            return 1L;
        return optional.get().getId() + 1;

    }
}
