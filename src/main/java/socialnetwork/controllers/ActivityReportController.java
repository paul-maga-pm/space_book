package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import socialnetwork.Run;
import socialnetwork.domain.entities.FriendshipDto;
import socialnetwork.domain.entities.Message;
import socialnetwork.domain.entities.MessageDto;
import socialnetwork.domain.entities.User;
import socialnetwork.service.SocialNetworkService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class ActivityReportController {
    private SocialNetworkService service;
    private User loggedUser;


    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @FXML
    ListView<MessageDto> messagesListView;
    ObservableList<MessageDto> messageObservableList = FXCollections.observableArrayList();

    @FXML
    ListView<FriendshipDto> friendshipsListView;
    ObservableList<FriendshipDto> friendshipDtoObservableList = FXCollections.observableArrayList();

    @FXML
    Spinner<Integer> monthChooser;

    @FXML
    Spinner<Integer> yearChooser;

    @FXML
    Button saveAsButton;

    @FXML
    Button loadActivityReportsButton;

    @FXML
    void initialize(){
        messagesListView.setItems(messageObservableList);
        messagesListView.setCellFactory(list -> new ListCell<MessageDto>(){
            @Override
            protected void updateItem(MessageDto item, boolean empty){
                super.updateItem(item, empty);
                if(item == null || empty){
                    setText(null);
                }else{
                    setText(item.getText() + " from " + item.getSender().getUserName());
                }
            }
        });

        friendshipsListView.setItems(friendshipDtoObservableList);
        friendshipsListView.setCellFactory(list -> new ListCell<>(){
            @Override
            protected void updateItem(FriendshipDto item, boolean empty){
                super.updateItem(item, empty);
                if(item == null || empty){
                    setText(null);
                }else{
                    setText(item.getFriend().toString());
                }
            }
        });

        monthChooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1));
        yearChooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1990,
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getYear()));
    }

    @FXML
    void handleClickOnSaveAsButton(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)",
                "*.pdf");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(Run.getPrimaryStage());
        if (file != null) {
            String fileUrl = file.getAbsolutePath();
            try {
                //service.exportNewFriendsAndNewMessagesOfUserFromMonth(fileUrl, loggedUser.getId(), monthChooser.getValue());
                service.exportNewFriendsAndNewMessagesOFUserFromYearAndMonth(fileUrl,
                        loggedUser.getId(),
                        yearChooser.getValue(),
                        monthChooser.getValue());
            } catch (IOException e) {
                Run.showPopUpWindow("Warning", "Couldn't export report");
            }
        }
    }

    @FXML
    void handleClickOnActivityReportsButton(ActionEvent event){
        int month = monthChooser.getValue();
        int year = yearChooser.getValue();
        List<FriendshipDto> friendships = service.getAllNewFriendshipsOfUserFromYearAndMonth(loggedUser.getId(),
                year,
                month);
        friendshipDtoObservableList.setAll(friendships);

        List<MessageDto> messages = service.getMessagesReceivedByUserInYearAndMonth(loggedUser.getId(),year, month);
        messageObservableList.setAll(messages);
    }
}
