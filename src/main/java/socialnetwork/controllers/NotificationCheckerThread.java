package socialnetwork.controllers;

public class NotificationCheckerThread extends Thread{
    private NotificationChecker checker;

    public NotificationCheckerThread(NotificationChecker checker){
        super(checker);
    }
}
