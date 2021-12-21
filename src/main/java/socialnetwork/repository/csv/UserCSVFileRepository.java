package socialnetwork.repository.csv;


import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.CorruptedDataException;
import socialnetwork.exceptions.IoFileException;

/**
 * Implementation of AbstractCSVFileRepository for the User model
 */
public class UserCSVFileRepository extends AbstractCSVFileRepository<Long, User> {
    /**
     * Constructor that creates a new repository that will access the file found at the given path
     * @param filePath absolute path to the file
     * @throws IoFileException if the file can't be opened
     */
    public UserCSVFileRepository(String filePath) {
        super(filePath);
    }


    /**
     * Parses the given user into CSV format
     * @return a String in CSV in format "id,firstName,lastName"
     */
    @Override
    public String entityToString(User user) {
        return "" +
                user.getId() + "," +
                user.getFirstName().stripTrailing().stripLeading() + "," +
                user.getLastName().stripTrailing().stripLeading();
    }

    /**
     * Parses the string that respects csv format into a User object
     * @param rawUserString CSV string in format "id,firstName,lastName"
     * @throws CorruptedDataException if the given string doesn't respect the CSV format
     */
    @Override
    public User stringToEntity(String rawUserString) {
        String[] userStringAttributes = rawUserString.split(",");

        if(userStringAttributes.length != 3)
            throw new CorruptedDataException("user csv file is corrupted");

        long id;
        try {
            id = Long.parseLong(userStringAttributes[0].stripLeading().stripTrailing());
        } catch (NumberFormatException exception){
            throw new CorruptedDataException("user csv file is corrupted");
        }
        String firstName = userStringAttributes[1].stripLeading().stripTrailing();
        String lastName = userStringAttributes[2].stripLeading().stripTrailing();
        return new User(id, firstName, lastName);
    }
}
