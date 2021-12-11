package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controllers.AuthenticationController;
import socialnetwork.domain.models.FriendRequest;
import socialnetwork.domain.models.Friendship;
import socialnetwork.domain.models.User;
import socialnetwork.domain.models.UserCredential;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.RepositoryInterface;
import socialnetwork.repository.database.FriendRequestDatabaseRepository;
import socialnetwork.repository.database.FriendshipDatabaseRepository;
import socialnetwork.repository.database.UserCredentialDatabaseRepository;
import socialnetwork.repository.database.UserDatabaseRepository;
import socialnetwork.service.FriendRequestService;
import socialnetwork.service.NetworkService;
import socialnetwork.service.SocialNetworkUserService;
import socialnetwork.service.UserService;
import socialnetwork.utils.containers.UnorderedPair;

public class Run extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("authentication.fxml"));

        final String url = ApplicationContext.getProperty("socialnetwork.database.url");
        final String user = ApplicationContext.getProperty("socialnetwork.database.user");
        final String password = ApplicationContext.getProperty("socialnetwork.database.password");

        RepositoryInterface<Long, User> userRepo = new UserDatabaseRepository(url, user, password);
        RepositoryInterface<UnorderedPair<Long, Long>, Friendship> friendshipRepo =
                new FriendshipDatabaseRepository(url, user, password);
        RepositoryInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepo =
                new FriendRequestDatabaseRepository(url, user, password);
        RepositoryInterface<Long, UserCredential> credentialRepo = new UserCredentialDatabaseRepository(url, user, password);

        EntityValidatorInterface<Long, User> userVal = new UserValidator();
        EntityValidatorInterface<UnorderedPair<Long, Long>, Friendship> friendshipVal = new FriendshipValidator(userRepo);
        EntityValidatorInterface<UnorderedPair<Long, Long>, FriendRequest> friendRequestVal = new FriendRequestValidator(userRepo);
        EntityValidatorInterface<Long, UserCredential> signUpCredentialVal = new UserSignUpCredentialValidator(credentialRepo);

        UserService userService = new UserService(userRepo, credentialRepo, signUpCredentialVal, userVal);
        NetworkService networkService = new NetworkService(friendshipRepo, userRepo, friendshipVal);
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepo, friendshipRepo, friendRequestVal);

        SocialNetworkUserService socialNetworkUserService = new SocialNetworkUserService(userService, networkService, null, friendRequestService);


        Scene scene = new Scene(fxmlLoader.load());

        AuthenticationController controller = fxmlLoader.getController();
        controller.setService(socialNetworkUserService);

        stage.setTitle("Log in");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
