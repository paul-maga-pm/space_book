package socialnetwork.repository.csv;

import org.junit.jupiter.api.BeforeEach;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.models.MessageDto;
import socialnetwork.repository.MessageDtoRepositoryTestSetter;
import socialnetwork.repository.RepositoryInterface;

import java.io.*;

public class MessageDtoCSVRepositoryTest extends MessageDtoRepositoryTestSetter {
    String filePath = ApplicationContext.getProperty("service.messages.crud");
    MessageDtoCSVFileRepository testRepository = null;
    @Override
    public RepositoryInterface<Long, MessageDto> getRepository() {
        if(testRepository == null)
            testRepository = new MessageDtoCSVFileRepository(filePath);
        return testRepository;
    }

    void tearDown(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @BeforeEach
    void setUp(){
        tearDown();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for(var message : getTestData()){
                String line = "";
                line += message.getId() + "," +
                        message.getText() + "," +
                        message.getDate().toString();
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
