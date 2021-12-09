package socialnetwork.domain.validators;

import socialnetwork.domain.models.UserCredential;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.exceptions.UnimplementedMethodException;
import socialnetwork.repository.RepositoryInterface;

public class UserSignUpCredentialValidator
        implements EntityValidatorInterface<Long, UserCredential> {
    private RepositoryInterface<Long, UserCredential> credentialRepository;
    public UserSignUpCredentialValidator(RepositoryInterface<Long, UserCredential> credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Override
    public void validate(UserCredential credential) {
        validateThatCredentialIsUnique(credential);
        validateUsernameOfCredential(credential);
        validatePasswordOfCredential(credential);
    }

    private void validatePasswordOfCredential(UserCredential credential) {
        if(credential.getPassword().length() == 0)
            throw new InvalidEntityException("Password can't be empty");
    }

    private void validateUsernameOfCredential(UserCredential credential) {
        String username = credential.getUserName();

        if(!username.endsWith("@email.com"))
            throw new InvalidEntityException("Username is invalid");
    }

    private boolean credentialsHaveSameIdOrUserName(UserCredential credential1, UserCredential credential2){
        return credential1.getId().equals(credential2.getId()) || credential1.getUserName().equals(credential2.getUserName());
    }

    private void validateThatCredentialIsUnique(UserCredential credential) {
        for(var crtCredential : credentialRepository.getAll())
            if(credentialsHaveSameIdOrUserName(crtCredential, credential))
                throw new InvalidEntityException("Username is already used");
    }

    @Override
    public boolean isValid(UserCredential credential) {
        throw new UnimplementedMethodException();
    }
}
