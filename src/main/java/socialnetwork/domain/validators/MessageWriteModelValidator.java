package socialnetwork.domain.validators;

import socialnetwork.domain.models.MessageWriteModel;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;

public class MessageWriteModelValidator {
    private RepositoryInterface<Long, User> userRepository;
    public MessageWriteModelValidator(RepositoryInterface<Long, User> userRepository) {
        this.userRepository = userRepository;
    }

    public void validate(MessageWriteModel message){
        String errorMessage = "";
        Long senderId = message.getIdOfSender();

        if(userDoesntExist(senderId))
            errorMessage += "User with id " + senderId + " doesn't exist\n";

        for(Long id : message.getIdListOfReceivers()){
            if(senderId.equals(id))
                throw new InvalidEntityException("Id's of sender and receivers must be different");
            if(userDoesntExist(id))
                errorMessage += "User with id " + id + " doesn't exist\n";
        }
        if(errorMessage.length() > 0)
            throw new EntityNotFoundValidationException(errorMessage);

        if(message.getIdListOfReceivers().isEmpty())
            errorMessage += "Receivers' list can't be empty\n";
        if(message.getText().equals(""))
            errorMessage += "Text can't be empty.";
        if(errorMessage.length() > 0)
            throw new InvalidEntityException(errorMessage);
    }

    private boolean userDoesntExist(Long userId) {
        return userRepository.findById(userId).isEmpty();
    }
}
