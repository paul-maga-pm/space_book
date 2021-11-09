package socialnetwork.repository.csv;


import org.junit.jupiter.api.BeforeEach;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.repository.FriendshipRepositoryTestSetter;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.utils.containers.UnorderedPair;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FriendshipCSVFileRepositoryTest extends FriendshipRepositoryTestSetter {
    String TEST_FILE_PATH = ApplicationContext.getProperty("repository.csv.friendships.test");
    FriendshipCSVFileRepository testRepository;
    @Override
    public RepositoryInterface<UnorderedPair<Long, Long>, Friendship> getRepository() {
        if(testRepository == null)
            testRepository = new FriendshipCSVFileRepository(TEST_FILE_PATH);
        return testRepository;
    }

    void tearDown(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(TEST_FILE_PATH))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp(){
        tearDown();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(TEST_FILE_PATH))) {
            for(Friendship friendship : getTestData()){
                String line = "" + friendship.getId().first + "," + friendship.getId().second;
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
