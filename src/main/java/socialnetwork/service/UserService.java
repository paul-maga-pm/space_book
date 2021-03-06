package socialnetwork.service;


import socialnetwork.domain.entities.User;
import socialnetwork.domain.entities.UserCredential;
import socialnetwork.domain.validators.EntityValidator;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.*;
import socialnetwork.utils.containers.PasswordEncryptor;

import java.util.*;
import java.util.stream.Collectors;

public class UserService {
    private EntityValidator<Long, User> userValidator;
    private PagingUserRepository userRepository;
    private Repository<Long, UserCredential> credentialRepository;
    private EntityValidator<Long, UserCredential> signupCredentialValidator;
    private final PasswordEncryptor encryptor = new PasswordEncryptor();

    /**
     * Constructor of the service
     */
    public UserService(PagingUserRepository userRepository,
                       Repository<Long, UserCredential> credentialRepository,
                       EntityValidator<Long, UserCredential> signUpCredentialValidator,
                       EntityValidator<Long, User> userValidator) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.signupCredentialValidator = signUpCredentialValidator;
        this.userValidator = userValidator;
    }

    /**
     * Returns the user with the given username and password
     */
    public User loginUser(String userName, String password) {
        return findUserWithCredentials(userName, password);
    }

    /**
     * Adds a new User entity and returns it
     * @param profilePictureFile relative path to the Resource folder to the profile picture of the user
     */
    public User signUpUser(String firstName, String lastName, String userName, String password, String profilePictureFile){
        Long id = findAvailableId();

        password = encryptor.hash(password);

        UserCredential credential = new UserCredential(id, userName, password);
        signupCredentialValidator.validate(credential);

        User user = new User(id, firstName, lastName, profilePictureFile);
        userValidator.validate(user);

        userRepository.save(user);
        credentialRepository.save(credential);
        return user;
    }

    /**
     * Returns all user for which the string "firstName lastName" contains the given string
     */
    public List<User> findUsersThatHaveInTheirFullNameTheString(String str){
        if(str.length() < 3)
            return new ArrayList<>();

        final String lowerCasedStr = str.toLowerCase(Locale.ROOT);
        List<User> users = new ArrayList<>();
        for(var user : userRepository.getAll()){
            String firstName = user.getFirstName().toLowerCase(Locale.ROOT);
            String lastName = user.getLastName().toLowerCase(Locale.ROOT);
            if (firstName.concat(" ").concat(lastName).contains(lowerCasedStr))
                users.add(user);
        }
        return users;
    }

    private int currentPageIndex = 0;
    private int usersPerPageCount = 5;

    /**
     * Sets the number of users per page
     */
    public void setUsersPerPageCount(int usersPerPageCount) {
        this.usersPerPageCount = usersPerPageCount;
    }

    /**
     * Sets the current page
     */
    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    /**
     * Returns a list with all users from the given page that have in their full name (firstName + " " + lastName) the
     * String str
     */
    public List<User> getUsersByName(String str, int pageIndex){
        if (str.length() <= 3)
            return new ArrayList<>();
        str = str.toLowerCase(Locale.ROOT);
        currentPageIndex = pageIndex;
        PageableInterface pageable = new Pageable(currentPageIndex, usersPerPageCount);
        PageInterface<User> foundUsers = userRepository.findAllUsersByName(pageable, str);
        return foundUsers.getContent().collect(Collectors.toList());
    }

    /**
     * Returns the users from the next page that have in their name the given string
     */
    public List<User> getNextUsersByName(String str){
        currentPageIndex++;
        return getUsersByName(str, currentPageIndex);
    }

    /**
     * Returns the number of users that have in their full name the given string
     */
    public int getNumberOfUsersThatHaveInTheirNameTheString(String str){
        if (str.length() <= 3)
            return 0;
        str = str.toLowerCase(Locale.ROOT);
        return userRepository.countUsersThatHaveInTheirNameTheString(str);
    }

    /**
     * Returns a list with all users
     */
    public List<User> getAllUsers(){
        return userRepository.getAll();
    }

    /**
     * Returns an available id for adding a new user
     */
    private Long findAvailableId() {
        var optional = userRepository.getAll().stream()
                .max(Comparator.comparing(User::getId));

        if(optional.isEmpty())
            return 1L;
        return optional.get().getId() + 1;

    }

    /**
     * Returns the user with the given credentials
     * @throws EntityNotFoundValidationException if the user doesn't exist
     */
    private User findUserWithCredentials(String userName, String password){
        for(UserCredential credential : credentialRepository.getAll())
            if(credential.getUserName().equals(userName) &&
            encryptor.authenticate(password, credential.getPassword()))
                return userRepository.findById(credential.getId()).get();

        throw new EntityNotFoundValidationException("Username or password incorrect. User doesn't exist");
    }

    /**
     * Returns the user with the given id
     */
    public Optional<User> findUserById(Long senderId) {
        return userRepository.findById(senderId);
    }

    /**
     * Updates the given user and returns the old value
     */
    public Optional<User> updateUser(User newUser){
        return userRepository.update(newUser);
    }
}
