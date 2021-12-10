package socialnetwork.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import socialnetwork.HelloApplication;
import socialnetwork.service.SocialNetworkUserService;

public class UserPageController {
    private SocialNetworkUserService service;

    @FXML
    private Button logOutButton;

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
}
