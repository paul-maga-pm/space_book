package socialnetwork.exceptions;

public class InvalidNumericalValueException extends ExceptionBaseClass {
    public InvalidNumericalValueException(String exceptionMessage){
        super(exceptionMessage);
    }

    public InvalidNumericalValueException(Throwable cause) {
        super(cause);
    }
}
