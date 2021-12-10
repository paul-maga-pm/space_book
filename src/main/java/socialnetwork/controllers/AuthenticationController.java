package socialnetwork.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialnetwork.HelloApplication;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.service.SocialNetworkUserService;

public class AuthenticationController {
    private SocialNetworkUserService service;

    @FXML
    private Button loginButton;

    @FXML
    private Button signInButton;
    @FXML
    private TextField signInFirstNameTextField;
    @FXML
    private TextField signInLastNameTextField;
    @FXML
    private TextField signInEmailTextField;
    @FXML
    private PasswordField signInPasswordPasswordField;

    @FXML
    protected void openUserPage(Event event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Log In");
        stage.setScene(scene);
        stage.show();
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    protected void signInUser(Event event){
        String firstName = signInFirstNameTextField.getText();
        String lastName = signInLastNameTextField.getText();
        String email = signInEmailTextField.getText();
        String password = signInPasswordPasswordField.getText();
        try{
            service.signUpUserService(firstName, lastName, email, password);
            openUserPage(event);
        } catch (ExceptionBaseClass exception){
            showWarning(exception.getMessage());

        } catch(Exception e){
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        alert.setTitle("Warning");
        alert.showAndWait();
    }

    public void setService(SocialNetworkUserService socialNetworkUserService){
        this.service = socialNetworkUserService;
    }
}
