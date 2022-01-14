package socialnetwork.models;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import socialnetwork.domain.entities.FriendRequestDto;

public class PendingFriendRequestReceivedByUserModel extends NotificationModel{
    private FriendRequestDto dto;

    private EventHandler<ActionEvent> acceptHandler;
    private EventHandler<ActionEvent> declineHandler;
    public PendingFriendRequestReceivedByUserModel(FriendRequestDto dto,
                                                   EventHandler<ActionEvent> acceptHandler,
                                                   EventHandler<ActionEvent> declineHandler) {
        this.dto = dto;
        this.acceptHandler = acceptHandler;
        this.declineHandler = declineHandler;
    }
    @Override
    public Node getViewForModel() {
        HBox box = new HBox(10);
        String senderName = dto.getSender().getFirstName() + " ";
        senderName += dto.getSender().getLastName();
        Label contact = new Label(senderName + " sent you a friend request");
        Button acceptButton = new Button("Accept");
        acceptButton.setStyle("-fx-background-color:  #477998");
        Button declineButton = new Button("Decline");
        declineButton.setStyle("-fx-background-color:  #477998");
        acceptButton.setOnAction(acceptHandler);
        declineButton.setOnAction(declineHandler);
        Label notifDate = new Label(super.toString());
        box.getChildren().addAll(notifDate, contact, acceptButton, declineButton);
        acceptButton.setUserData(dto.getSender());
        declineButton.setUserData(dto.getSender());
        return box;
    }

    public FriendRequestDto getFriendRequestDto() {
        return dto;
    }
}
