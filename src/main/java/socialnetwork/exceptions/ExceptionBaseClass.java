package socialnetwork.exceptions;

public class ExceptionBaseClass extends RuntimeException{
    public ExceptionBaseClass(String exceptionMessage){
        super(exceptionMessage);
    }

    public ExceptionBaseClass(Throwable cause) {
        super(cause);
    }

    public ExceptionBaseClass(){
    }
}
