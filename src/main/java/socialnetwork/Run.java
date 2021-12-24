package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controllers.AuthenticationController;
import socialnetwork.domain.entities.FriendRequest;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.entities.UserCredential;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendRequestDatabaseRepository;
import socialnetwork.repository.database.FriendshipDatabaseRepository;
import socialnetwork.repository.database.UserCredentialDatabaseRepository;
import socialnetwork.repository.database.UserDatabaseRepository;
import socialnetwork.service.FriendRequestService;
import socialnetwork.service.NetworkService;
import socialnetwork.service.SocialNetworkService;
import socialnetwork.service.UserService;
import socialnetwork.utils.containers.UnorderedPair;

public class Run extends Application {
    private static Stage primaryStage;

    public SocialNetworkService createService(){

        final String url = ApplicationContext.getProperty("socialnetwork.database.url");
        final String user = ApplicationContext.getProperty("socialnetwork.database.user");
        final String password = ApplicationContext.getProperty("socialnetwork.database.password");

        Repository<Long, User> userRepo = new UserDatabaseRepository(url, user, password);
        Repository<UnorderedPair<Long, Long>, Friendship> friendshipRepo =
                new FriendshipDatabaseRepository(url, user, password);
        Repository<UnorderedPair<Long, Long>, FriendRequest> friendRequestRepo =
                new FriendRequestDatabaseRepository(url, user, password);
        Repository<Long, UserCredential> credentialRepo = new UserCredentialDatabaseRepository(url, user, password);

        EntityValidator<Long, User> userVal = new UserValidator();
        EntityValidator<UnorderedPair<Long, Long>, Friendship> friendshipVal = new FriendshipValidator(userRepo);
        EntityValidator<UnorderedPair<Long, Long>, FriendRequest> friendRequestVal = new FriendRequestValidator(userRepo);
        EntityValidator<Long, UserCredential> signUpCredentialVal = new UserSignUpCredentialValidator(credentialRepo);

        UserService userService = new UserService(userRepo, credentialRepo, signUpCredentialVal, userVal);
        NetworkService networkService = new NetworkService(friendshipRepo, userRepo, friendshipVal);
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepo, friendshipRepo, friendRequestVal);

        return new SocialNetworkService(userService,
                networkService,
                friendRequestService,
                null);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(Run.class.getResource("authentication.fxml"));
        Scene scene = new Scene(loader.load());
        AuthenticationController controller = loader.getController();
        controller.setService(createService());
        primaryStage.setScene(scene);
        primaryStage.show();
//        PendingFriendRequestView view = new PendingFriendRequestView(new User(1L, "Michael", "Corleone"),
//                LocalDateTime.now());
//        Scene scene = new Scene(view);
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }

    public static Stage getPrimaryStage(){
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void showPopUpWindow(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

}
