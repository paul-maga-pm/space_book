package socialnetwork.models;

import javafx.scene.Node;
import javafx.scene.control.Label;
import socialnetwork.domain.entities.FriendRequestDto;

import java.time.format.DateTimeFormatter;

public class ApprovedFriendRequestModel extends NotificationModel {
    private FriendRequestDto dto;

    public ApprovedFriendRequestModel(FriendRequestDto dto){
        this.dto = dto;
    }

    @Override
    public Node getViewForModel() {
        return new Label(super.toString() + " You accepted the friend request sent by " +
                dto.getSender() + " on " +
                dto.getFriendRequest().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
