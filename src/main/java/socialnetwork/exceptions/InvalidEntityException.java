package socialnetwork.exceptions;

public class InvalidEntityException extends ExceptionBaseClass{
    public InvalidEntityException(String exceptionMessage){
        super(exceptionMessage);
    }

    public InvalidEntityException(Throwable cause) {
        super(cause);
    }
}
