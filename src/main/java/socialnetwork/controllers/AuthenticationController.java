package socialnetwork.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
            User signedUser = service.signUpUserService(firstName, lastName, email, password, "rick.jpg");
            openUserPage(signedUser);
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());

        } catch(Exception e){
        }
    }

    private void openUserPage(User loggedUser) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainMenuController controller = fxmlLoader.getController();
        controller.setLoggedUser(loggedUser);
        controller.setService(service);
        Run.getPrimaryStage().setScene(scene);
    }

    public void setService(SocialNetworkService socialNetworkService){
        this.service = socialNetworkService;
    }
}
