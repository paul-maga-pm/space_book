package socialnetwork.models;

import javafx.scene.Node;
import javafx.scene.control.Label;
import socialnetwork.domain.entities.FriendRequestDto;

import java.time.format.DateTimeFormatter;

public class RejectedFriendRequestModel extends NotificationModel {
    public RejectedFriendRequestModel(FriendRequestDto dto) {
        this.dto = dto;
    }

    private FriendRequestDto dto;


    @Override
    public Node getViewForModel() {
        return new Label(super.toString() + " You declined friend request sent by " +
                dto.getSender() + " on " +
                dto.getFriendRequest().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")));
    }
}
