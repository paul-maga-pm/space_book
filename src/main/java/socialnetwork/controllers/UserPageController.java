package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    ObservableList<FriendshipDto> userFriendsModel = FXCollections.observableArrayList();

    @FXML
    ToggleButton friendRequestToggleButton;

    @FXML
    ImageView userPageProfileView;

    @FXML
    Button removeFriendButton;

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setUserThatOwnsThePage(User userThatOwnsThePage) {
        this.userThatOwnsThePage = userThatOwnsThePage;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @FXML
    void initialize(){
        userFriendsListView.setItems(userFriendsModel);
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
        setStateOfRemoveFriendButton();
        loadProfilePictureOnPage();
    }

    private void setStateOfFriendRequestButton(Optional<FriendRequest> friendRequest) {
        if(loggedUser.getId().equals(userThatOwnsThePage.getId())) {
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
                else if(status == Status.PENDING && friendRequest.get().getReceiverId().equals(loggedUser.getId())) {
                    friendRequestToggleButton.setText("Send friend request");
                    friendRequestToggleButton.setDisable(true);
                } else if(status == Status.APPROVED) {
                    friendRequestToggleButton.setText("Friends");
                    friendRequestToggleButton.setDisable(true);
                }
                else {
                    friendRequestToggleButton.setText("Sent");
                    friendRequestToggleButton.setSelected(true);
                }
            }
        }
    }

    private void setStateOfRemoveFriendButton(){
        if(!loggedUser.getId().equals(userThatOwnsThePage.getId()))
            removeFriendButton.setVisible(false);
    }

    private void loadProfilePictureOnPage() {
        Image profilePic = new Image(String.valueOf(Run.class.getResource("rick.jpg")));
        userPageProfileView.setImage(profilePic);
    }

    private void loadFriendsOfPageOwner() {
        userFriendsModel.setAll(service.findAllFriendsOfUser(userThatOwnsThePage.getId()));

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
                friendRequestToggleButton.setText("Sent");
            }
            else if (friendRequest.get().getStatus().equals(Status.PENDING)){
                service.withdrawFriendRequest(loggedUser.getId(), userThatOwnsThePage.getId());
                friendRequestToggleButton.setText("Send friend request");
            }
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());
            return;
        }
    }

    @FXML
    void handleClickOnRemoveFriendButton(ActionEvent event){
        FriendshipDto dto = userFriendsListView.getSelectionModel().getSelectedItem();

        if (dto != null){
            service.removeFriendshipService(loggedUser.getId(), dto.getFriend().getId());
            userFriendsModel.remove(dto);
        }
    }
}
