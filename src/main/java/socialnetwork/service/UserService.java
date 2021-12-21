package socialnetwork.service;

import socialnetwork.domain.entities.User;
import socialnetwork.domain.entities.UserCredential;
import socialnetwork.domain.validators.EntityValidator;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.repository.Repository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserService {
    private EntityValidator<Long, User> userValidator;
    private Repository<Long, User> userRepository;
    private Repository<Long, UserCredential> credentialRepository;
    private EntityValidator<Long, UserCredential> signupCredentialValidator;

    public UserService(Repository<Long, User> userRepository,
                       Repository<Long, UserCredential> credentialRepository,
                       EntityValidator<Long, UserCredential> signUpCredentialValidator,
                       EntityValidator<Long, User> userValidator) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.signupCredentialValidator = signUpCredentialValidator;
        this.userValidator = userValidator;
    }


    public User loginUser(String userName, String password) {
        return findUserWithCredentials(userName, password);
    }

    public User signUpUser(String firstName, String lastName, String userName, String password){
        Long id = findAvailableId();

        UserCredential credential = new UserCredential(id, userName, password);
        signupCredentialValidator.validate(credential);

        User user = new User(id, firstName, lastName);
        userValidator.validate(user);

        userRepository.save(user);
        credentialRepository.save(credential);
        return user;
    }

    // Each user from the returned list will contain their username
    public List<User> findUsersThatHaveInTheirFullNameTheString(String str){
        if(str.length() < 3)
            return new ArrayList<>();

        final String lowerCasedStr = str.toLowerCase(Locale.ROOT);
        Predicate<User> fullNameContainsString
                = user -> user.getFirstName().toLowerCase(Locale.ROOT).contains(lowerCasedStr)
                || user.getLastName().toLowerCase(Locale.ROOT).contains(lowerCasedStr);
        List<User> users = userRepository
                .getAll()
                .stream()
                .filter(fullNameContainsString)
                .collect(Collectors.toList());
        return users;
    }

    public List<User> getAllUsers(){
        return userRepository.getAll();
    }

    private Long findAvailableId() {
        var optional = userRepository.getAll().stream()
                .max(Comparator.comparing(User::getId));

        if(optional.isEmpty())
            return 1L;
        return optional.get().getId() + 1;

    }

    private User findUserWithCredentials(String userName, String password){
        for(UserCredential credential : credentialRepository.getAll())
            if(credential.getUserName().equals(userName) && credential.getPassword().equals(password))
                return userRepository.findById(credential.getId()).get();

        throw new EntityNotFoundValidationException("Username or password incorrect. User doesn't exist");
    }
}
