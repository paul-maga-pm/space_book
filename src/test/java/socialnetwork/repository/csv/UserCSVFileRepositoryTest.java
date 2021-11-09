package socialnetwork.repository.csv;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.User;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.UserRepositoryTestSetter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class UserCSVFileRepositoryTest extends UserRepositoryTestSetter {
    UserCSVFileRepository testRepository;
    String TEST_FILE_PATH = ApplicationContext.getProperty("repository.csv.users.test");
    @Override
    public RepositoryInterface<Long, User> getRepository() {
        if(testRepository == null)
            testRepository = new UserCSVFileRepository(TEST_FILE_PATH);
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
            for(User user : getTestData()){
                String line = "" + user.getId() + "," + user.getFirstName() + "," + user.getLastName();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}