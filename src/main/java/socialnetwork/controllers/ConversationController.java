package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.Run;
import socialnetwork.domain.entities.ConversationDto;
import socialnetwork.domain.entities.MessageDto;
import socialnetwork.domain.entities.User;
import socialnetwork.events.NewConversationHasBeenCreatedEvent;
import socialnetwork.service.Observer;
import socialnetwork.service.SocialNetworkService;

import java.io.IOException;
import java.time.LocalDateTime;

public class ConversationController implements Observer<NewConversationHasBeenCreatedEvent> {
    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    private SocialNetworkService service;
    private User loggedUser;

    @FXML
    Button createConversationButton;

    @FXML
    Button sendMessageButton;

    @FXML
    TextField messageTextField;

    @FXML
    ListView<ConversationDto> conversationListView;

    @FXML
    ListView<MessageDto> messageListView;

    ObservableList<ConversationDto> conversationDtoObservableList = FXCollections.observableArrayList();
    ObservableList<MessageDto> messageDtoObservableList = FXCollections.observableArrayList();
    @FXML
    void initialize(){
        conversationListView.setItems(conversationDtoObservableList);
        conversationListView.setCellFactory(list -> new ListCell<ConversationDto>(){
            @Override
            protected void updateItem(ConversationDto item, boolean empty){
                super.updateItem(item, empty);
                if(item == null || empty){
                    setText(null);
                }else{
                    setText(item.getName() + " " + item.getDescription());
                }
            }
        });
        conversationListView.getSelectionModel().selectedItemProperty().addListener((x, y, z) ->
                handleSelectionChangeInConversationListView());

        messageListView.setItems(messageDtoObservableList);
        /*messageListView.setCellFactory(list -> new ListCell<MessageDto>(){
            @Override
            protected void updateItem(MessageDto item, boolean empty){
                super.updateItem(item, empty);
                if(item == null || empty){
                    setText(null);
                }else{
                    setText(item.getSender().getFirstName() + " " +
                            item.getSender().getLastName()  + ":" +
                            item.getText()
                    );
                }
            }
        });*/
        messageListView.setCellFactory(list -> new ChatCell());
    }

    class ChatCell extends ListCell<MessageDto>{

        HBox graphic = new HBox();
        Text message = new Text();
        Label label = new Label();
        public ChatCell(){
            graphic.getChildren().add(new VBox(label, message));
        }

        @Override
        public void updateItem(MessageDto item, boolean empty){
            super.updateItem(item, empty);
            if(!empty && (item != null)){
                label.setText(item.getSender().getId().equals(loggedUser.getId()) ? "" : item.getSender().getFirstName()+" "+item.getSender().getLastName());
                label.setStyle("-fx-font-size: 9px;");
                message.setText(item.getText());
                graphic.setAlignment(item.getSender().getId().equals(loggedUser.getId()) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                setGraphic(graphic);
            } else {
                message.setText("");
                setGraphic(null);
            }
        }

    }
    void handleSelectionChangeInConversationListView(){
        var dto = conversationListView.getSelectionModel().getSelectedItem();
        System.out.printf("%s %s\n", dto.getName(), dto.getDescription());

        messageDtoObservableList.setAll(dto.getMessages());
    }

    @FXML
    void handleClickOnSendButton(ActionEvent event){
        String messageText = messageTextField.getText().strip();

        if (messageText.length() == 0)
            return;

        var conversationId = conversationListView.getSelectionModel()
                .getSelectedItem()
                .getId();

        var date = LocalDateTime.now();
        service.sendMessageInConversation(loggedUser.getId(), conversationId, messageText, date);
        var message = new MessageDto(loggedUser, messageText, date);
        messageDtoObservableList.add(message);
        conversationListView.getSelectionModel().getSelectedItem().addMessage(message);

        messageTextField.clear();
    }

    public void loadExistingConversations(){
        var convos = service.getConversationsOfUser(loggedUser.getId());
        conversationDtoObservableList.setAll(convos);
    }

    @Override
    public void update(NewConversationHasBeenCreatedEvent newConversationHasBeenCreatedEvent) {
        var conversation = newConversationHasBeenCreatedEvent.getConversation();
        this.conversationDtoObservableList.add(conversation);
    }

    @FXML
    void handleClickOnCreateConversation(ActionEvent event) throws IOException {
        try{
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Run.class.getResource("conversation-participants-selection.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(Run.class.getResource("main-stylesheet.css").toExternalForm());
            ConversationCreationController controller = loader.getController();
            controller.setService(service);
            controller.setLoggedUser(loggedUser);
            controller.setStage(stage);
            stage.initOwner(Run.getPrimaryStage());
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception){
            exception.printStackTrace();
            System.exit(1);
        }
    }
}
