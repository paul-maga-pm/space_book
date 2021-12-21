package socialnetwork.domain.entities;

/**
 * Class that turns a String into and Enum type
 */
public class StatusSupplier {
    public static Status getStatus(String status){
        if(status.equals("PENDING"))
            return Status.PENDING;
        if(status.equals("APPROVED"))
            return Status.APPROVED;
        return Status.REJECTED;
    }
}
