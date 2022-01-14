package socialnetwork.models;

import javafx.scene.Node;
import javafx.scene.control.Label;
import socialnetwork.domain.entities.FriendRequestDto;

import java.time.format.DateTimeFormatter;

public class ApprovedFriendRequestReceivedByUserModel extends NotificationModel {
    private FriendRequestDto dto;

    public ApprovedFriendRequestReceivedByUserModel(FriendRequestDto dto){
        this.dto = dto;
    }

    @Override
    public Node getViewForModel() {
        String senderName = dto.getSender().getFirstName() + " " + dto.getSender().getLastName();
        return new Label(super.toString() + " You accepted the friend request sent by " +
                senderName + " on " +
                dto.getFriendRequest().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
