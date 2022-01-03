package socialnetwork.domain.entities;

public class NotificationStatusSupplier {
    public static NotificationStatus getStatus(String status){
        if(status.equals("SUBSCRIBED"))
            return NotificationStatus.SUBSCRIBED;
        return NotificationStatus.UNSUBSCRIBED;
    }
}
