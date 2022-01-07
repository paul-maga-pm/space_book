package socialnetwork.pagination;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import socialnetwork.domain.entities.User;
import socialnetwork.service.SocialNetworkService;

import java.util.List;

public abstract class UserSearchResultPagination extends Pagination {
    private int itemsPerPageNumber;
    private int itemsNumber;
    private String usernameSearchField;
    private SocialNetworkService service;


    public SocialNetworkService getService() {
        return service;
    }

    public void setService(SocialNetworkService service) {
        this.service = service;
    }



    public UserSearchResultPagination(int usersCount, int usersPerPageNumber, String usernameSearchField){
        this.usernameSearchField = usernameSearchField;
        this.itemsNumber = usersCount;
        this.itemsPerPageNumber = usersPerPageNumber;
        this.setPageFactory(new UserSearchResultPageFactory());
        this.setPageCount(calculateNumberOfPages(itemsNumber, itemsPerPageNumber));
        this.setCurrentPageIndex(0);
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
            List<User> usersOnPage = service.getUsersByName(usernameSearchField, pageIndex);
            for (var user : usersOnPage){

                Hyperlink link = new Hyperlink("" + user);
                link.setUserData(user);
                EventHandler<ActionEvent> handler = UserSearchResultPagination.this::handleClickOnUserLink;
                link.setOnAction(handler);
                box.getChildren().add(link);
            }
            return box;
        }
    }

    public abstract void handleClickOnUserLink(ActionEvent event);

}
