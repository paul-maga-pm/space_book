package socialnetwork.repository.database;

public class DatabaseCleaner {
    public static void clearDatabase(){
        FriendshipDatabaseTableSetter.tearDown();
        MessagesSenderReceiverDatabaseTableSetter.tearDown();
        UserDatabaseTableSetter.tearDown();
        MessageDatabaseTableSetter.tearDown();
    }
}
