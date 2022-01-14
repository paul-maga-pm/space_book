package socialnetwork.models;

import javafx.scene.Node;
import javafx.scene.control.Label;
import socialnetwork.domain.entities.FriendRequestDto;

import java.time.format.DateTimeFormatter;

public class ApprovedFriendRequestSentByUserModel extends NotificationModel{
    private FriendRequestDto dto;

    public ApprovedFriendRequestSentByUserModel(FriendRequestDto dto) {
        this.dto = dto;
    }

    @Override
    public Node getViewForModel() {
        String senderName = dto.getReceiver().getFirstName() + " " + dto.getReceiver().getLastName();

        return new Label(super.toString() + " " +
                senderName + " accepted your friend request sent on " +
                dto.getFriendRequest().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
