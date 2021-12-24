package socialnetwork.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import socialnetwork.Run;
import socialnetwork.service.SocialNetworkService;

public class UserSearchResultController {
    SocialNetworkService service;

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    @FXML
    Pagination linkToUserPage;

    @FXML
    void handleClickOnLinkToUserPage(ActionEvent event) throws Exception{
        FXMLLoader loader = new FXMLLoader(Run.class.getResource("user-page.fxml"));
        Scene scene = new Scene(loader.load());
        UserPageController controller = loader.getController();
        controller.setService(service);
        Run.getPrimaryStage().setScene(scene);
    }

}
