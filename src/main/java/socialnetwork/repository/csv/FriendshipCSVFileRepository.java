package socialnetwork.repository.csv;


import socialnetwork.domain.entities.Friendship;
import socialnetwork.exceptions.CorruptedDataException;
import socialnetwork.exceptions.IoFileException;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;

/**
 * Implementation of AbstractCSVFileRepository for Friendship model
 */
public class FriendshipCSVFileRepository
        extends AbstractCSVFileRepository<UnorderedPair<Long, Long>, Friendship> {


    /**
     * Constructor that creates a new repository that will access the file found at the given path
     * @param filePath absolute path to the file
     * @throws IoFileException if the file can't be opened
     */
    public FriendshipCSVFileRepository(String filePath) {
        super(filePath);
    }

    /**
     * Parses the given friendship into CSV format
     * @return a String in CSV in format "idOfFirstUser,idOfSecondUser"
     */
    @Override
    public String entityToString(Friendship friendship) {
        return "" + friendship.getId().first +
                "," + friendship.getId().second +
                "," + friendship.getDate().toString();
    }

    /**
     * Parses the string that respects csv format into a Friendship object
     * @param rawFriendshipString CSV string in format "idOfFirstUser,idOfSecondUser"
     * @throws CorruptedDataException if the given string doesn't respect the CSV format
     */
    @Override
    public Friendship stringToEntity(String rawFriendshipString) {
        String[] attributes = rawFriendshipString.split(",");

        if(attributes.length != 3)
            throw new CorruptedDataException(getFilePath() + " csv file is corrupted");
        Long idOfFirstUser;
        Long idOfSecondUser;
        LocalDateTime date;
        try {
            idOfFirstUser = Long.parseLong(attributes[0].stripLeading().stripTrailing());
            idOfSecondUser = Long.parseLong(attributes[1].stripLeading().stripTrailing());
            date = LocalDateTime.parse(attributes[2].stripLeading().stripTrailing());
        } catch (NumberFormatException exception) {
            throw new CorruptedDataException("friendship csv file is corrupted");
        }
        return new Friendship(idOfFirstUser, idOfSecondUser, date);
    }
}
