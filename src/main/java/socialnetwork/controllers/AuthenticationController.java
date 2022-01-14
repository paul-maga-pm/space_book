package socialnetwork.controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.Run;
import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.service.SocialNetworkService;

public class AuthenticationController {
    private SocialNetworkService service;

    @FXML
    private Button loginButton;
    @FXML
    private TextField loginEmailTextField;
    @FXML
    private PasswordField loginPasswordPasswordField;

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
    private ImageView appLogoImageView;

    @FXML
    void initialize(){
        Image image = new Image(String.valueOf(Run.class.getResource("logo.png")));
        appLogoImageView.setImage(image);
    }

    @FXML
    protected void loginUser(Event event){
        String email = loginEmailTextField.getText();
        String password = loginPasswordPasswordField.getText();
        try{
            User loggedUser = service.loginUserService(email, password);
            openUserPage(loggedUser);
        } catch(ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());

        } catch(Exception e){
        }
    }

    @FXML
    protected void signInUser(Event event){
        String firstName = signInFirstNameTextField.getText();
        String lastName = signInLastNameTextField.getText();
        String email = signInEmailTextField.getText();
        String password = signInPasswordPasswordField.getText();
        try{
            User signedUser = service.signUpUserService(firstName, lastName, email, password, "profile-picture.jpg");
            openUserPage(signedUser);
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());

        } catch(Exception e){
        }
    }

    private void openUserPage(User loggedUser) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Run.class.getResource("main-stylesheet.css").toExternalForm());
        MainMenuController controller = fxmlLoader.getController();
        controller.setLoggedUser(loggedUser);
        controller.setService(service);
        controller.startEventNotificationChecking();
        controller.handleClickOnHomeButton(new ActionEvent());
        Run.getPrimaryStage().setScene(scene);
    }

    public void setService(SocialNetworkService socialNetworkService){
        this.service = socialNetworkService;
    }
}
