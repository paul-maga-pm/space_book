package socialnetwork.repository.csv;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.exceptions.IoFileException;

public class FIleDoesntExistTest {
    @Test
    void usersFileDoesntExist(){
        Assertions.assertThrows(IoFileException.class, () -> {
            var repository = new UserCSVFileRepository("ads/asd/asd/asd.csv");
        });
    }

    @Test
    void friendshipsFileDoesntExist(){
        Assertions.assertThrows(IoFileException.class, () -> {
            var repository = new FriendshipCSVFileRepository("ads/asd/asd/asd.csv");
        });
    }
}
