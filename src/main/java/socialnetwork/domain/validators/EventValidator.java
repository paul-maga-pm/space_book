package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Event;
import socialnetwork.exceptions.InvalidEntityException;

public class EventValidator implements EntityValidator<Long, Event>{
    @Override
    public void validate(Event event) {
        String errorMessage = "";
        if(event.getId().compareTo(0L) < 0)
            errorMessage = errorMessage.concat("Id can't be negative.\n");
        if(event.getDescription().compareTo("") == 0)
            errorMessage = errorMessage.concat("Name can't be empty.\n");
        if(event.getImageFile().compareTo("") == 0)
            errorMessage = errorMessage.concat("Image file can't be empty.\n");
        if(errorMessage.compareTo("") != 0)
            throw new InvalidEntityException(errorMessage);
    }
}
