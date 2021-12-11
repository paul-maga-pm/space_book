package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialnetwork.HelloApplication;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.service.SocialNetworkUserService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class UserPageController {
    private SocialNetworkUserService service;
    ObservableList<User> modelUsers = FXCollections.observableArrayList();

    @FXML
    private TextField searchUserTextField;
    @FXML
    private ListView<User> listViewUsers;
    @FXML
    private Button sendFriendRequestButton;

    @FXML
    private TableView tableViewFriends;

    @FXML
    private TableView tableViewFriendRequests;

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
    }

    @FXML
    protected void sendFriendRequest(){
        User user = listViewUsers.getSelectionModel().getSelectedItem();
        if(user != null){
            try{
                service.sendFriendRequestService(user.getId());
            }catch(ExceptionBaseClass exception) {
                showWarning(exception.getMessage());
            }
        } else {
            showWarning("Must select a user!");
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
        //((Node)(event.getSource())).getScene().getWindow().hide();
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    public void setService(SocialNetworkUserService socialNetworkUserService){
        this.service = socialNetworkUserService;
    }

    private void handleSearch(){
        modelUsers.setAll(service.findUsersThatHaveInTheirFullNameTheString(searchUserTextField.getText()));
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        alert.setTitle("Warning");
        alert.showAndWait();
    }
}
