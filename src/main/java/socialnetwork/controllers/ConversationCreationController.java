package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import socialnetwork.Run;
import socialnetwork.domain.entities.ConversationDto;
import socialnetwork.domain.entities.User;
import socialnetwork.events.NewConversationHasBeenCreatedEvent;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.pagination.UserSearchResultPagination;
import socialnetwork.service.Observer;
import socialnetwork.service.SocialNetworkService;

import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConversationCreationController {
    private SocialNetworkService service;
    private User loggedUser;
    private Stage stage;

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    TextField conversationNameTextField;

    @FXML
    TextField conversationDescriptionTextField;

    @FXML
    TextField usernameTextField;

    @FXML
    HBox usersListLayout;

    @FXML
    ListView<User> participantsListView;

    ObservableList<User> participantsObservableList = FXCollections.observableArrayList();

    @FXML
    void initialize(){
        participantsListView.setCellFactory(list -> new ListCell<User>(){
            @Override
            protected void updateItem(User item, boolean empty){
                super.updateItem(item, empty);
                if(item == null || empty){
                    setText(null);
                }else{
                    setText(item.toString());
                }
            }
        });
        participantsListView.setItems(participantsObservableList);
    }



    @FXML
    void handleClickOnSearchUserButton(){
        var str = usernameTextField.getText().strip();
        str = parseSearchUserName(str);
        var count = service.getNumberOfUsersThatHaveInTheirNameTheString(str);
        UserSearchResultPagination pagination = new UserSearchResultPagination(count, 4, str) {
            @Override
            public void handleClickOnUserLink(ActionEvent event) {
                var source = (Hyperlink) event.getSource();
                User clickedUser = (User)source.getUserData();
                if (!participantsObservableList.contains(clickedUser) &&
                        !Objects.equals(clickedUser.getId(), loggedUser.getId()))
                    participantsObservableList.add(clickedUser);
            }
        };
        pagination.setService(service);
        service.setNumberOfUserPerFiltrationByNamePage(4);
        var children = usersListLayout.getChildren();
        if (children.size() == 2)
            children.set(1, pagination);
        else if (children.size() == 1)
            children.add(pagination);

    }

    @FXML
    public void handleClickOnCreateConversationButton(ActionEvent event) {
        List<Long> participants = participantsObservableList.stream()
                        .map(User::getId)
                        .collect(Collectors.toList());

        try {
            service.createConversation(loggedUser.getId(),
                    conversationNameTextField.getText().strip(),
                    conversationDescriptionTextField.getText().strip(),
                    participants);
        } catch (ExceptionBaseClass exception){
            Run.showPopUpWindow("Warning", exception.getMessage());
        }
    }

    private String parseSearchUserName(String userNameSearchField) {
        userNameSearchField = userNameSearchField.strip();
        String[] attributes = userNameSearchField.split(" ");

        if (attributes.length == 0)
            return "";

        if (attributes.length == 1)
            return attributes[0];

        StringBuilder fullName = new StringBuilder(attributes[0]);

        for(int i = 1; i < attributes.length; i++)
            fullName.append(" ").append(attributes[i]);
        return fullName.toString();
    }
}
