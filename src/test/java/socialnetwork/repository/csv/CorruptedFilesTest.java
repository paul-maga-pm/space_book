package socialnetwork.repository.csv;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.config.ApplicationContext;
import socialnetwork.exceptions.CorruptedDataException;

public class CorruptedFilesTest {
    @Test
    void usersFileIsCorrupted(){
        Assertions.assertThrows(CorruptedDataException.class, () -> {
           var repository = new UserCSVFileRepository(ApplicationContext.getProperty("repository.csv.invalid_format"));
        });

        Assertions.assertThrows(CorruptedDataException.class, () -> {
            var repository = new UserCSVFileRepository(ApplicationContext.getProperty("repository.csv.users.corrupted_file1"));
        });

        Assertions.assertThrows(CorruptedDataException.class, () -> {
            var repository = new UserCSVFileRepository(ApplicationContext.getProperty("repository.csv.users.corrupted_file2"));
        });
    }

    @Test
    void friendshipsFileIsCorrupted(){
        Assertions.assertThrows(CorruptedDataException.class, () -> {
            var repository = new FriendshipCSVFileRepository(ApplicationContext.getProperty("repository.csv.invalid_format"));
        });

        Assertions.assertThrows(CorruptedDataException.class, () -> {
            var repository = new FriendshipCSVFileRepository(ApplicationContext.getProperty("repository.csv.friendships.corrupted_file1"));
        });

        Assertions.assertThrows(CorruptedDataException.class, () -> {
            var repository = new FriendshipCSVFileRepository(ApplicationContext.getProperty("repository.csv.friendships.corrupted_file2"));
        });
    }
}
