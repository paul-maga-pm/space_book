package socialnetwork.models;

import javafx.scene.Node;
import javafx.scene.control.Label;
import socialnetwork.domain.entities.FriendRequestDto;

import java.time.format.DateTimeFormatter;

public class RejectedFriendRequestReceivedByUserModel extends NotificationModel {
    public RejectedFriendRequestReceivedByUserModel(FriendRequestDto dto) {
        this.dto = dto;
    }

    private FriendRequestDto dto;


    @Override
    public Node getViewForModel() {
        String senderName = dto.getSender().getFirstName() + " " + dto.getSender().getLastName();
        return new Label(super.toString() + " You declined friend request sent by " +
                senderName + " on " +
                dto.getFriendRequest().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
