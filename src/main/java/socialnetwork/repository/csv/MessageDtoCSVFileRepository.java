package socialnetwork.repository.csv;

import socialnetwork.domain.models.MessageDto;
import socialnetwork.exceptions.CorruptedDataException;
import socialnetwork.exceptions.IoFileException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class MessageDtoCSVFileRepository extends AbstractCSVFileRepository<Long, MessageDto> {
    /**
     * Constructor that creates a new repository that will access the file found at the given path
     *
     * @param filePath absolute path to the file
     * @throws IoFileException if the file can't be opened
     */
    public MessageDtoCSVFileRepository(String filePath) {
        super(filePath);
    }

    @Override
    public String entityToString(MessageDto entity) {
        return "" + entity.getId() + "," +
                entity.getText() + "," +
                entity.getDate().toString();
    }

    @Override
    public MessageDto stringToEntity(String rawEntityString) {
        String[] stringAttributes = rawEntityString.split(",");

        if(stringAttributes.length != 3)
            throw new CorruptedDataException(getFilePath() + " has rows with not 3 attributes");

        long messageId;
        try{
            messageId = Long.parseLong(stringAttributes[0]);
        } catch (NumberFormatException exception) {
            throw new CorruptedDataException("Invalid value for id in file " + getFilePath());
        }

        String text = stringAttributes[1].stripTrailing().stripLeading();
        LocalDateTime date;

        try{
            date = LocalDateTime.parse(stringAttributes[2]);
        } catch (DateTimeParseException e) {
            throw new CorruptedDataException("Invalid value for date in file " + getFilePath());
        }
        return new MessageDto(messageId, text, date);
    }
}
