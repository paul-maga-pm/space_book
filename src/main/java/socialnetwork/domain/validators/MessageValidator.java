package socialnetwork.domain.validators;

import socialnetwork.domain.models.MessageReadModel;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.RepositoryInterface;

/**
 * Validator for MessageReadModel model
 */
public class MessageValidator implements EntityValidatorInterface<Long, MessageReadModel>{
    private RepositoryInterface<Long, User> userRepository;

    /**
     * Constructor that creates a new validator that accesses the given user repository for message validation
     * @param userRepository repository containing the users
     * @throws IllegalArgumentException if userRepository is null
     */
    public MessageValidator(RepositoryInterface<Long, User> userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Validates the existence of the users in the repository (given in the constructor) of the given message
     * @param message Entity that will be validated
     * @throws EntityNotFoundValidationException if the users in the given message don't exist in the user repository
     *         InvalidEntityException if one of the receiving users has the same id as the sender user, or if
     *                                the receivers' list is empty, or if the text is empty
     */
    @Override
    public void validate(MessageReadModel message) {
        String errorMessage = "";
        Long senderId = message.getSender().getId();

        if(!checkIfUserExits(senderId))
            errorMessage += "User with id " + senderId + " doesn't exist\n";

        for(User user : message.getReceivers()){
            if(senderId == user.getId())
                throw new InvalidEntityException("Id's of sender and receivers must be different");
            if(!checkIfUserExits(user.getId()))
                errorMessage += "User with id " + user.getId() + " doesn't exist\n";
        }
        if(errorMessage.length() > 0)
            throw new EntityNotFoundValidationException(errorMessage);

        if(message.getReceivers().isEmpty())
            errorMessage += "Receivers' list can't be empty\n";
        if(message.getText().equals(""))
            errorMessage += "Text can't be empty.";
        if(errorMessage.length() > 0)
            throw new InvalidEntityException(errorMessage);
    }

    /**
     * Validates the existence of the users in the repository (given in the constructor) of the given message
     * @param message Entity that will be validated
     * @return true if the users of the message exist in the repository, false otherwise
     */
    @Override
    public boolean isValid(MessageReadModel message) {
        if(!checkIfUserExits(message.getSender().getId()))
            return false;

        for(User user: message.getReceivers())
            if(!checkIfUserExits(user.getId()))
                return false;

        return true;
    }

    /**
     * Checks if the user exists in the repository
     * @param id identifier of user
     * @return true if the user exists in the repository, false otherwise
     */
    private boolean checkIfUserExits(Long id){
        return userRepository.findById(id).isPresent();
    }
}
