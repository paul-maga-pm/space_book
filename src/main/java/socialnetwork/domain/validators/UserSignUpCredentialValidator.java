package socialnetwork.domain.validators;

import socialnetwork.domain.entities.UserCredential;
import socialnetwork.exceptions.InvalidEntityException;
import socialnetwork.repository.Repository;

public class UserSignUpCredentialValidator
        implements EntityValidator<Long, UserCredential> {
    private Repository<Long, UserCredential> credentialRepository;
    public UserSignUpCredentialValidator(Repository<Long, UserCredential> credentialRepository) {
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

        if(!username.endsWith("@email.com") && !username.endsWith("@gmail.com"))
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

}
