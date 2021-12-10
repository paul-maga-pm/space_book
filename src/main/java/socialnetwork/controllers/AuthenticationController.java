package socialnetwork.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import socialnetwork.HelloApplication;

public class AuthenticationController {
    @FXML
    private Button loginButton;

    @FXML
    private Button signInButton;

    @FXML
    protected void openUserPage(Event event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Log In");
        stage.setScene(scene);
        stage.show();
        //((Node)(event.getSource())).getScene().getWindow().hide();
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    protected void showWarning(Event event) {
        Alert alert = new Alert(Alert.AlertType.NONE, "Could not Sign In", ButtonType.OK);
        alert.setTitle("Warning");
        alert.showAndWait();
    }
}
