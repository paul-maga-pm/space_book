package socialnetwork.exceptions;

public class EntityNotFoundValidationException extends InvalidEntityException{
    public EntityNotFoundValidationException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public EntityNotFoundValidationException(Throwable cause) {
        super(cause);
    }
}
