package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Event;
import socialnetwork.domain.entities.EventParticipant;
import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.containers.UnorderedPair;

public class EventParticipantValidator implements EntityValidator<UnorderedPair<Long, Long>, EventParticipant>{
    private Repository<Long, User> userRepository;
    private Repository<Long, Event> eventRepository;

    public EventParticipantValidator(Repository<Long, User> userRepository, Repository<Long, Event> eventRepository){
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void validate(EventParticipant eventParticipant) {
        String errorMessage = "";
        Long userId = eventParticipant.getId().first;
        Long eventId = eventParticipant.getId().second;
        if(userRepository.findById(userId).isEmpty())
            errorMessage = errorMessage.concat("User with id " + userId + " doesn't exist!\n");
        if(eventRepository.findById(eventId).isEmpty())
            errorMessage = errorMessage.concat("Event with id " + eventId + " doesn't exist!\n");
        if(errorMessage.length() > 0)
            throw new EntityNotFoundValidationException(errorMessage);
    }
}
