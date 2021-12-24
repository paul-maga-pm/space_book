package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.Run;
import socialnetwork.domain.entities.FriendRequest;
import socialnetwork.domain.entities.FriendshipDto;
import socialnetwork.domain.entities.Status;
import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.service.SocialNetworkService;

import java.util.Optional;

public class UserPageController {
    private SocialNetworkService service;
    private User userThatOwnsThePage;
    private User loggedUser;

    @FXML
    Label firstNameLabel;

    @FXML
    Label lastNameLabel;

    @FXML
    Label usernameLabel;

    @FXML
    ListView<FriendshipDto> userFriendsListView;

    @FXML
    ToggleButton friendRequestToggleButton;

    @FXML
    ImageView userPageProfileView;

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setUserThatOwnsThePage(User userThatOwnsThePage) {
        this.userThatOwnsThePage = userThatOwnsThePage;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void loadUserInformationOnPage(){
        loadContactInfoOfUserOnPage();

        Optional<FriendRequest> friendRequest;
        try {
            loadFriendsOfPageOwner();
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());
            return;
        }

        try {
            friendRequest = service.findOneFriendRequestService(loggedUser.getId(),
                    userThatOwnsThePage.getId());
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());
            return;
        }
        setStateOfFriendRequestButton(friendRequest);
        loadProfilePictureOnPage();
    }

    private void setStateOfFriendRequestButton(Optional<FriendRequest> friendRequest) {
        if(loggedUser == userThatOwnsThePage) {
            friendRequestToggleButton.setVisible(false);
        }
        else {
            if (friendRequest.isEmpty())
                friendRequestToggleButton.setText("Send friend request");
            else {
                Status status = friendRequest.get().getStatus();
                if (status == Status.REJECTED) {
                    friendRequestToggleButton.setText("Send friend request");
                    friendRequestToggleButton.setDisable(true);
                }
                else {
                    friendRequestToggleButton.setText(status.toString());
                    friendRequestToggleButton.setDisable(true);
                }
            }
        }
    }

    private void loadProfilePictureOnPage() {
        Image profilePic = new Image(String.valueOf(Run.class.getResource("rick.jpg")));
        userPageProfileView.setImage(profilePic);
    }

    private void loadFriendsOfPageOwner() {
        userFriendsListView
                .setItems(FXCollections.observableArrayList(service.findAllFriendsOfUser(userThatOwnsThePage.getId())));

    }

    private void loadContactInfoOfUserOnPage() {
        firstNameLabel.setText(userThatOwnsThePage.getFirstName());
        lastNameLabel.setText(userThatOwnsThePage.getLastName());
        usernameLabel.setText(userThatOwnsThePage.getUserName());
    }

    @FXML
    void handleClickOnFriendRequestButton(ActionEvent event){
        try {
            Optional<FriendRequest> friendRequest = service.findOneFriendRequestService(loggedUser.getId(),
                    userThatOwnsThePage.getId());

            if (friendRequest.isEmpty()) {
                service.sendFriendRequestService(loggedUser.getId(), userThatOwnsThePage.getId());
                friendRequestToggleButton.setDisable(true);
                friendRequestToggleButton.setText("Pending");
            }
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());
            return;
        }
    }
}
