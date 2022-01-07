package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import socialnetwork.Run;
import socialnetwork.domain.entities.*;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.service.SocialNetworkService;

import java.io.File;
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

    @FXML
    Tooltip tooltip = new Tooltip();

    @FXML
    private FileChooser fileChooser = new FileChooser();

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

        if(loggedUser.getId().equals(userThatOwnsThePage.getId())){
            tooltip.setText("Click to choose\n a profile picture!");
            Tooltip.install(userPageProfileView, tooltip);
        }


        setStateOfFriendRequestButton(friendRequest);
        setStateOfRemoveFriendButton();
        loadProfilePictureOnPage();
        addMouseClickEventForImageView();

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
        String profilePictureFile = userThatOwnsThePage.getProfilePictureFile();
        int index = profilePictureFile.lastIndexOf('\\');
        profilePictureFile = profilePictureFile.substring(index+1);
        Image profilePic = new Image(String.valueOf(Run.class.getResource(profilePictureFile)));
        userPageProfileView.setImage(profilePic);
    }

    private void addMouseClickEventForImageView(){
        if(loggedUser.getId().equals(userThatOwnsThePage.getId())){
            userPageProfileView.setOnMouseClicked((MouseEvent mouseEvent) -> handleModifyProfilePictureEvent());
        }
    }

    public void handleModifyProfilePictureEvent() {
        File file = fileChooser.showOpenDialog(Run.getPrimaryStage());
        if(file != null){
            loggedUser.setProfilePictureFile(file.getAbsolutePath());
            service.updateUserService(loggedUser);
            loadProfilePictureOnPage();
        }
        else{
            Run.showPopUpWindow("Warning", "Must choose an image file!");
        }
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
