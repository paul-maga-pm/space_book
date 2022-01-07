package socialnetwork.pagination;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import socialnetwork.Run;
import socialnetwork.controllers.UserPageController;
import socialnetwork.domain.entities.User;
import socialnetwork.service.SocialNetworkService;

import java.io.IOException;
import java.util.List;

public class UserSearchResultPaginationWithOpeningUserPage extends UserSearchResultPagination{
    private User loggedUser;
    private BorderPane userPageNode;

    public UserSearchResultPaginationWithOpeningUserPage(int usersCount, int usersPerPageNumber, String usernameSearchField) {
        super(usersCount, usersPerPageNumber, usernameSearchField);
    }

    public void setLoggedUser(User loggedUser){
        this.loggedUser = loggedUser;
    }

    public void setMainMenuBorderPane(BorderPane parent){
        this.userPageNode = parent;
    }

    @Override
    public void handleClickOnUserLink(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Run.class.getResource("user-page.fxml"));
            Scene scene = new Scene(loader.load());
            UserPageController controller = loader.getController();
            var source = (Hyperlink) event.getSource();
            User clickedUser = (User)source.getUserData();
            controller.setService(getService());
            controller.setUserThatOwnsThePage(clickedUser);
            controller.setLoggedUser(loggedUser);
            controller.loadUserInformationOnPage();
            userPageNode.setCenter(scene.getRoot());

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
