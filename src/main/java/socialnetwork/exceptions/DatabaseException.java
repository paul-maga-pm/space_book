package socialnetwork.exceptions;

public class DatabaseException extends ExceptionBaseClass{
    public DatabaseException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
