package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controllers.AuthenticationController;
import socialnetwork.domain.entities.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.repository.paging.PagingUserRepository;
import socialnetwork.service.*;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        Repository<Long, Event> eventRepository = new EventDatabaseRepository(url, user, password);
        Repository<UnorderedPair<Long, Long>, EventParticipant> eventParticipantRepository = new EventParticipantDatabaseRepository(url, user, password);

        EntityValidator<Long, User> userVal = new UserValidator();
        EntityValidator<UnorderedPair<Long, Long>, Friendship> friendshipVal = new FriendshipValidator(userRepo);
        EntityValidator<UnorderedPair<Long, Long>, FriendRequest> friendRequestVal = new FriendRequestValidator(userRepo);
        EntityValidator<Long, UserCredential> signUpCredentialVal = new UserSignUpCredentialValidator(credentialRepo);
        EntityValidator<Long, Event> eventValidator = new EventValidator();
        EntityValidator<UnorderedPair<Long, Long>, EventParticipant> eventParticipantValidator = new EventParticipantValidator(userRepo, eventRepository);

        UserService userService = new UserService((PagingUserRepository) userRepo,
                credentialRepo, signUpCredentialVal, userVal);
        NetworkService networkService = new NetworkService(friendshipRepo, userRepo, friendshipVal);
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepo, friendshipRepo, friendRequestVal);
        EventService eventService = new EventService(eventValidator, eventRepository, eventParticipantValidator, eventParticipantRepository);

        ConversationDatabaseRepository conversationRepo = new ConversationDatabaseRepository(url, user, password);
        MessageDatabaseRepository messageRepo = new MessageDatabaseRepository(url, user, password);
        ConversationParticipationDatabaseRepository participationRepo =
                new ConversationParticipationDatabaseRepository(url, user, password);
        ConversationService conversationService = new ConversationService(userRepo,
                conversationRepo,
                participationRepo,
                messageRepo,
                new MessageValidator(participationRepo),
                new ConversationValidator(),
                new ConversationParticipationValidator(userRepo, conversationRepo));
        return new SocialNetworkService(userService,
                networkService,
                friendRequestService,
                conversationService,
                eventService);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(Run.class.getResource("authentication.fxml"));
        Scene scene = new Scene(loader.load());
        AuthenticationController controller = loader.getController();
        controller.setService(createService());
        scene.getStylesheets().add(Run.class.getResource("authentication-stylesheet.css").toExternalForm());
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
