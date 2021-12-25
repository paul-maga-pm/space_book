package socialnetwork.pagination;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import socialnetwork.domain.entities.User;

import java.util.List;

public abstract class UserSearchResultPagination extends Pagination {
    private int itemsPerPageNumber;
    private int itemsNumber;

    private List<User> userList;

    public UserSearchResultPagination(List<User> usersList, int usersPerPageNumber){
        this.itemsNumber = usersList.size();
        this.itemsPerPageNumber = usersPerPageNumber;
        this.userList = usersList;
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
            for (int i = pageIndex * itemsPerPageNumber;
                 i < (pageIndex + 1) * itemsPerPageNumber && i < userList.size();
                 i++) {

                Hyperlink link = new Hyperlink("" + userList.get(i));
                link.setUserData(userList.get(i));
                EventHandler<ActionEvent> handler = UserSearchResultPagination.this::handleClickOnUserLink;
                link.setOnAction(handler);
                box.getChildren().add(link);
            }
            return box;
        }
    }

    public abstract void handleClickOnUserLink(ActionEvent event);

}
