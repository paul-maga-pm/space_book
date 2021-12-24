package socialnetwork.pagination;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Pagination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import socialnetwork.domain.entities.User;
import socialnetwork.Run;
import socialnetwork.controllers.UserPageController;
import socialnetwork.service.SocialNetworkService;

import java.io.IOException;
import java.util.List;

public class UserSearchResultPagination extends Pagination {
    private User loggedUser;
    private int itemsPerPageNumber;
    private int itemsNumber;
    private SocialNetworkService service;
    private BorderPane mainMenuBorderPane;
    private List<User> searchResultUserList;


    public UserSearchResultPagination(List<User> usersList, int usersPerPageNumber){
        this.itemsNumber = usersList.size();
        this.itemsPerPageNumber = usersPerPageNumber;
        this.searchResultUserList = usersList;
        this.setPageFactory(new UserSearchResultPageFactory());
        this.setPageCount(calculateNumberOfPages(itemsNumber, itemsPerPageNumber));
        this.setCurrentPageIndex(0);
    }

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setMainMenuBorderPane(BorderPane mainMenuBorderPane) {
        this.mainMenuBorderPane = mainMenuBorderPane;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }


    private int calculateNumberOfPages(int itemsNumber, int itemsPerPageNumber) {
        int numberOfPages = 0;
        if(itemsNumber < itemsPerPageNumber) {
            numberOfPages = 1;
        }
        else {
            numberOfPages = itemsNumber / itemsPerPageNumber;
            if(itemsNumber % itemsPerPageNumber != 0)
                numberOfPages++;
        }
        return numberOfPages;
    }

    private class UserSearchResultPageFactory implements Callback<Integer, Node> {
        @Override
        public Node call(Integer pageIndex) {
            VBox box = new VBox(5);
            for (int i = pageIndex * itemsPerPageNumber;
                 i < (pageIndex + 1) * itemsPerPageNumber && i < searchResultUserList.size();
                 i++) {

                Hyperlink link = new Hyperlink("" + searchResultUserList.get(i));
                User clickedUser = searchResultUserList.get(i);
                ClickOnLinkToUserPageEventHandler handler = new ClickOnLinkToUserPageEventHandler(clickedUser);
                link.setOnAction(handler);
                box.getChildren().add(link);
            }
            return box;
        }
    }

    private class ClickOnLinkToUserPageEventHandler implements EventHandler<ActionEvent>{
        private User clickedUser;

        public ClickOnLinkToUserPageEventHandler(User clickedUser) {
            this.clickedUser = clickedUser;
        }

        @Override
        public void handle(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(Run.class.getResource("user-page.fxml"));
                Scene scene = new Scene(loader.load());
                UserPageController controller = loader.getController();
                controller.setService(service);
                controller.setUserThatOwnsThePage(clickedUser);
                controller.setLoggedUser(loggedUser);
                controller.loadUserInformationOnPage();
                mainMenuBorderPane.setCenter(scene.getRoot());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
