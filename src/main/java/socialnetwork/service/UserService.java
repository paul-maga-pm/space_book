package socialnetwork.service;

import socialnetwork.domain.models.User;
import socialnetwork.domain.models.UserCredential;
import socialnetwork.domain.validators.EntityValidatorInterface;
import socialnetwork.exceptions.EntityNotFoundValidationException;
import socialnetwork.repository.RepositoryInterface;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserService {
    private EntityValidatorInterface<Long, User> userValidator;
    private RepositoryInterface<Long, User> userRepository;
    private RepositoryInterface<Long, UserCredential> credentialRepository;
    private EntityValidatorInterface<Long, UserCredential> signupCredentialValidator;

    public UserService(RepositoryInterface<Long, User> userRepository,
                       RepositoryInterface<Long, UserCredential> credentialRepository,
                       EntityValidatorInterface<Long, UserCredential> signUpCredentialValidator,
                       EntityValidatorInterface<Long, User> userValidator) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.signupCredentialValidator = signUpCredentialValidator;
        this.userValidator = userValidator;
    }


    public Long loginUser(String userName, String password) {
        return findIdOfUserWithCredentials(userName, password);
    }

    public Long signUpUser(String firstName, String lastName, String userName, String password){
        Long id = findAvailableId();

        UserCredential credential = new UserCredential(id, userName, password);
        signupCredentialValidator.validate(credential);

        User user = new User(id, firstName, lastName);
        userValidator.validate(user);

        userRepository.save(user);
        credentialRepository.save(credential);
        return id;
    }

    public List<User> findUsersThatHaveInTheirFullNameTheString(String str){
        final String lowerCasedStr = str.toLowerCase(Locale.ROOT);
        Predicate<User> fullNameContainsString
                = user -> user.getFirstName().toLowerCase(Locale.ROOT).contains(lowerCasedStr)
                || user.getLastName().toLowerCase(Locale.ROOT).contains(lowerCasedStr);
        return userRepository
                .getAll()
                .stream()
                .filter(fullNameContainsString)
                .collect(Collectors.toList());
    }

    public Long findIdOfUserWithUsername(String userName){
        for(UserCredential credential : credentialRepository.getAll())
            if(credential.getUserName().equals(userName))
                return credential.getId();
        throw new EntityNotFoundValidationException("User with username " + userName + " doesn't exist");
    }

    private Long findAvailableId() {
        Long maxId = -1L;
        for(User user : userRepository.getAll())
            if(user.getId() > maxId)
                maxId = user.getId() + 1;
        return maxId + 1;
    }

    private Long findIdOfUserWithCredentials(String userName, String password){
        for(UserCredential credential : credentialRepository.getAll())
            if(credential.getUserName().equals(userName) && credential.getPassword().equals(password))
                return credential.getId();

        throw new EntityNotFoundValidationException("Username or password incorrect. User doesn't exist");
    }
}
