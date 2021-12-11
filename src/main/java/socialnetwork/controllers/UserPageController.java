package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.HelloApplication;
import socialnetwork.domain.models.FriendRequest;
import socialnetwork.domain.models.Status;
import socialnetwork.domain.models.StatusSupplier;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.service.SocialNetworkUserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserPageController {
    private SocialNetworkUserService service;
    ObservableList<User> modelUsers = FXCollections.observableArrayList();
    ObservableList<ModelFriendRequest> modelFriendRequests = FXCollections.observableArrayList();
    ObservableList<User> modelFriends = FXCollections.observableArrayList();

    @FXML
    private TextField searchUserTextField;
    @FXML
    private ListView<User> listViewUsers;
    @FXML
    private Button sendFriendRequestButton;

    @FXML
    private TableView<User> tableViewFriends;
    @FXML
    private TableColumn<User, String> tableColumnFirstName;
    @FXML
    private TableColumn<User, String> tableColumnLastName;


    @FXML
    private TableView<ModelFriendRequest> tableViewFriendRequests;
    @FXML
    private TableColumn<ModelFriendRequest, String> tableColumnFrom;
    @FXML
    private TableColumn<ModelFriendRequest, String> tableColumnStatus;
    @FXML
    private TableColumn<ModelFriendRequest, LocalDateTime> tableColumnDate;
    @FXML
    private Button acceptFriendRequestButton;
    @FXML
    private Button rejectFriendRequestButton;

    @FXML
    private Button logOutButton;

    @FXML
    public void initialize(){
        searchUserTextField.textProperty().addListener(e -> handleSearch());

        listViewUsers.setCellFactory(list -> new ListCell<User>(){
            @Override
            protected void updateItem(User item, boolean empty){
                super.updateItem(item, empty);
                if(item == null || empty){
                    setText(null);
                }else{
                    setText(item.getFirstName()+" "+item.getLastName()+" "+item.getUserName());
                }
            }
        });
        listViewUsers.setItems(modelUsers);

        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<ModelFriendRequest, String>("from"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<ModelFriendRequest, String>("status"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<ModelFriendRequest, LocalDateTime>("date"));
        tableViewFriendRequests.setItems(modelFriendRequests);

        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableViewFriends.setItems(modelFriends);
    }

    @FXML
    protected void sendFriendRequest(){
        User user = listViewUsers.getSelectionModel().getSelectedItem();
        if(user != null){
            try{
                service.sendFriendRequestService(user.getId());
            }catch(ExceptionBaseClass exception) {
                showWarning("Warning", exception.getMessage());
            }
        } else {
            showWarning("Warning","Must select a user!");
        }
    }

    @FXML
    protected void acceptFriendRequest(){
        ModelFriendRequest modelFriendRequest = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        if(modelFriendRequest != null){
            try{
                Optional<FriendRequest> existingFriendRequest = service.acceptOrRejectFriendRequestService(modelFriendRequest.getUser().getId(), Status.APPROVED);
                if(existingFriendRequest.isPresent())
                    if(existingFriendRequest.get().getStatus().equals(Status.PENDING)){
                        modelFriendRequest.setStatus("APPROVED");
                        modelFriendRequests.set(tableViewFriendRequests.getSelectionModel().getSelectedIndex(), modelFriendRequest);
                } else{
                    showWarning("Information", "Could not accept");
                }
            } catch(ExceptionBaseClass exception){
                showWarning("Warning", exception.getMessage());
            }
        } else {
            showWarning("Warning", "Must select a friend request!");
        }
    }

    @FXML
    protected void rejectFriendRequest(){
        ModelFriendRequest modelFriendRequest = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        if(modelFriendRequest != null){
            try{
                Optional<FriendRequest> existingFriendRequest = service.acceptOrRejectFriendRequestService(modelFriendRequest.getUser().getId(), Status.REJECTED);
                if(existingFriendRequest.isPresent())
                    if(existingFriendRequest.get().getStatus().equals(Status.PENDING)){
                        modelFriendRequest.setStatus("REJECTED");
                        modelFriendRequests.set(tableViewFriendRequests.getSelectionModel().getSelectedIndex(), modelFriendRequest);
                    } else {
                        showWarning("Information", "Could not reject");
                    }
            } catch(ExceptionBaseClass exception){
                showWarning("Warning", exception.getMessage());
            }
        } else {
            showWarning("Warning", "Must select a friend request!");
        }
    }

    @FXML
    protected void returnToLogin(Event event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("authentication.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        AuthenticationController controller = fxmlLoader.getController();
        controller.setService(service);
        Stage stage = new Stage();
        stage.setTitle("Log In");
        stage.setScene(scene);
        stage.show();
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    public void setService(SocialNetworkUserService socialNetworkUserService){
        this.service = socialNetworkUserService;

        Map<FriendRequest, User> friendRequestsOfUserMap = service.getAllFriendRequestsOfLoggedUser();
        List<ModelFriendRequest> friendRequestsOfUser = new ArrayList<>();
        for(Map.Entry<FriendRequest, User> set: friendRequestsOfUserMap.entrySet()){
            friendRequestsOfUser.add(new ModelFriendRequest(set.getKey(), set.getValue()));
        }
        modelFriendRequests.setAll(friendRequestsOfUser);

        Map<Optional<User>, LocalDateTime> friendsMap = service.findAllFriendsOfLoggedUser();
        List<User> friends = new ArrayList<>();
        for(Map.Entry<Optional<User>, LocalDateTime> set: friendsMap.entrySet()){
            friends.add(set.getKey().get());
        }
        modelFriends.setAll(friends);
    }

    private void handleSearch(){
        modelUsers.setAll(service.findUsersThatHaveInTheirFullNameTheString(searchUserTextField.getText()));
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public static class ModelFriendRequest{
        private FriendRequest friendRequest;
        private User user;
        private String from;
        private String status;
        private LocalDateTime date;

        public ModelFriendRequest(FriendRequest friendRequest, User user) {
            this.friendRequest = friendRequest;
            this.user = user;
            this.from = user.getFirstName()+" "+user.getLastName();
            this.status = friendRequest.getStatus().toString();
            this.date = friendRequest.getDate();
        }

        public String getFrom() {
            return from;
        }

        public String getStatus() {
            return status;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public FriendRequest getFriendRequest() {
            return friendRequest;
        }

        public User getUser() {
            return user;
        }

        public void setStatus(String status) {
            this.friendRequest.setStatus(StatusSupplier.getStatus(status));
            this.status = status;
        }
    }
}
