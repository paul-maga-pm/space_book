package socialnetwork.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import socialnetwork.Run;
import socialnetwork.domain.entities.FriendRequestDto;
import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.pagination.UserSearchResultPagination;
import socialnetwork.service.SocialNetworkService;

import java.io.IOException;
import java.util.List;

public class MainMenuController {
    SocialNetworkService service;
    User loggedUser;

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public User getLoggedUser(){
        return loggedUser;
    }

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    @FXML
    TextField userSearchTextField;
    @FXML
    Button userSearchButton;
    @FXML
    Button notificationsButton;

    @FXML
    BorderPane mainMenuBorderPane;

    @FXML
    void handleUserSearchButtonClick(ActionEvent event){
        String userNameSearchField = userSearchTextField.getText().strip();

        String userName = parseSearchUserName(userNameSearchField);
        List<User> foundUsers;
        try {
            foundUsers = service.findUsersThatHaveInTheirFullNameTheString(userName);
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());
            return;
        }
        if(foundUsers.size() == 0) {
            mainMenuBorderPane.setCenter(null);
            return;
        }

        UserSearchResultPagination pagination = new UserSearchResultPagination(foundUsers, 10);
        pagination.setService(service);
        pagination.setLoggedUser(loggedUser);
        pagination.setMainMenuBorderPane(mainMenuBorderPane);
        mainMenuBorderPane.setCenter(pagination);
    }

    private String parseSearchUserName(String userNameSearchField) {
        userNameSearchField = userNameSearchField.strip();
        String[] attributes = userNameSearchField.split(" ");

        if (attributes.length == 0)
            return "";

        if (attributes.length == 1)
            return attributes[0];

        StringBuilder fullName = new StringBuilder(attributes[0]);

        for(int i = 1; i < attributes.length; i++)
            fullName.append(" ").append(attributes[i]);
        return fullName.toString();
    }


    @FXML
    void handleClickOnLogoutButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Run.class.getResource("authentication.fxml"));
        Scene scene = new Scene(loader.load());
        AuthenticationController controller = loader.getController();
        controller.setService(service);
        Run.getPrimaryStage().setScene(scene);
    }

    @FXML
    void handleClickOnHomeButton(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(Run.class.getResource("user-page.fxml"));
        Scene scene = new Scene(loader.load());
        UserPageController controller = loader.getController();
        controller.setService(service);
        controller.setUserThatOwnsThePage(loggedUser);
        controller.setLoggedUser(loggedUser);
        controller.loadUserInformationOnPage();
        mainMenuBorderPane.setCenter(scene.getRoot());
    }

    @FXML
    void handleClickOnNotificationsButton(ActionEvent event) throws IOException{
        List<FriendRequestDto> friendRequestDtoList;

        try{
            friendRequestDtoList = service.getAllFriendRequestsSentToUser(loggedUser.getId());
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());
            return;
        }

        if (friendRequestDtoList.size() == 0)
            return;

        FXMLLoader loader = new FXMLLoader(Run.class.getResource("notification-pagination.fxml"));
        Scene scene = new Scene(loader.load());
        NotificationPaginationController controller = loader.getController();
        controller.setService(service);
        controller.setLoggedUser(loggedUser);
        controller.init();
        mainMenuBorderPane.setCenter(scene.getRoot());
    }
}
