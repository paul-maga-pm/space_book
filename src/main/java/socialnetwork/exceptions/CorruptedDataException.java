package socialnetwork.exceptions;

public class CorruptedDataException extends ExceptionBaseClass {
    public CorruptedDataException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public CorruptedDataException(Throwable cause) {
        super(cause);
    }
}
