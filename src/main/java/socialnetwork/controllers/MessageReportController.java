package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import socialnetwork.Run;
import socialnetwork.domain.entities.Message;
import socialnetwork.domain.entities.MessageDto;
import socialnetwork.domain.entities.User;
import socialnetwork.pagination.UserSearchResultPagination;
import socialnetwork.service.SocialNetworkService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MessageReportController {
    private SocialNetworkService service;
    private User loggedUser;
    private User selectedMessageSender;


    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }
    @FXML
    ListView<Message> messageListView;
    ObservableList<Message> messageObservableList = FXCollections.observableArrayList();

    @FXML
    Spinner<Integer> monthChooser;

    @FXML
    Button previewReportButton;


    @FXML
    void handleClickOnPreviewReportButton(ActionEvent event){
        if (selectedMessageSender == null)
            return;

        int month = monthChooser.getValue();
        List<Message> messages = service.getMessagesReceivedByUserSentByOtherUserInMonth(loggedUser.getId(),
                selectedMessageSender.getId(),
                month);
        messageObservableList.setAll(messages);
    }
    @FXML
    Button searchUserButton;

    @FXML
    TextField userNameTextField;

    @FXML
    HBox listAndPaginationLayout;

    @FXML
    Button saveAsButton;

    @FXML
    void handleClickOnSaveAsButton(ActionEvent event){
        if (selectedMessageSender == null)
            Run.showPopUpWindow("Warning", "You must select a user!");
        else {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)",
                    "*.pdf");
            fileChooser.getExtensionFilters().add(extensionFilter);
            File file = fileChooser.showSaveDialog(Run.getPrimaryStage());

            if (file != null) {
                String fileUrl = file.getAbsolutePath();
                try {
                    service.exportMessagesReceivedByUserSentByOtherUserInMonth(fileUrl,
                            loggedUser.getId(),
                            selectedMessageSender.getId(),
                            monthChooser.getValue());
                } catch (IOException e) {
                    Run.showPopUpWindow("Warning", "Couldn't export report");
                }
            }
        }
    }

    @FXML
    void handleClickOnSearchUserButton(ActionEvent event){
        var str = userNameTextField.getText().strip();
        str = parseSearchUserName(str);
        var count = service.getNumberOfUsersThatHaveInTheirNameTheString(str);
        UserSearchResultPagination pagination = new UserSearchResultPagination(count, 4, str) {
            @Override
            public void handleClickOnUserLink(ActionEvent event) {
                Hyperlink source = (Hyperlink)event.getSource();
                selectedMessageSender = (User)source.getUserData();
            }
        };
        pagination.setService(service);
        service.setNumberOfUserPerFiltrationByNamePage(4);
        var children = listAndPaginationLayout.getChildren();
        if (children.size() == 2)
            children.set(1, pagination);
        else if (children.size() == 1)
            children.add(pagination);
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

    @FXML
    void initialize(){
        messageListView.setItems(messageObservableList);
        messageListView.setCellFactory(list -> new ListCell<>(){
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null || empty){
                    setText(null);
                }else{
                    setText(item.getText());
                }
            }
        });

        monthChooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1));
    }
}
