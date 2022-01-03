package socialnetwork.domain.entities;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstraction of a user in the network
 */
public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String userName;
    private List<User> friendsList = new ArrayList<>();
    private String profilePictureFile;

    /**
     * Constructor that creates a new user with the given id, first name and last name
     * @param id identifier of the user
     * @param firstName first name of the user
     * @param lastName last name of the user
     */
    public User(Long id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(Long id, String firstName, String lastName, String profilePictureFile) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePictureFile = profilePictureFile;
    }

    public void setUserName(String newUserName){
        this.userName = newUserName;
    }

    public String getUserName(){
        return userName;
    }

    public String getProfilePictureFile() {
        return profilePictureFile;
    }

    public void setProfilePictureFile(String profilePictureFile) {
        this.profilePictureFile = profilePictureFile;
    }

    /**
     * Copy constructor that creates a new User with the data of other
     * @param other user that will be copied
     */
    public User(User other) {
        super(other.getId());
        this.firstName = other.firstName;
        this.lastName = other.lastName;
    }

    /**
     * Getter method for firstName
     * @return first name of the user
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Getter method for lastName
     * @return last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter method for friends list
     * @return friends of the user
     */
    public List<User> getFriendsList() {
        return friendsList;
    }

    /**
     * Setter method for friends list
     * @param friends new value for the list of friends of this
     */
    public void setFriendsList(List<User> friends){
        this.friendsList = friends;
    }


    /**
     * Checks if this and o are equal by value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName);
    }

    /**
     * Returns hashCode of this User
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName);
    }

    /**
     * Parses this User into String format
     * @return Stringin format "ID: {id}, First Name: {firstName}, Last Name: {lastName}"
     */
    @Override
    public String toString() {
        String userStringFormat = "%s %s %s";
        return String.format(userStringFormat, firstName, lastName, userName);
    }
}
