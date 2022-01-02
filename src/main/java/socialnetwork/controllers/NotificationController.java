package socialnetwork.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.util.Callback;
import socialnetwork.domain.entities.FriendRequest;
import socialnetwork.domain.entities.FriendRequestDto;
import socialnetwork.domain.entities.Status;
import socialnetwork.domain.entities.User;
import socialnetwork.models.*;
import socialnetwork.service.SocialNetworkService;
import socialnetwork.utils.containers.UnorderedPair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationController {
    private SocialNetworkService service;
    private User loggedUser;

    private SortedList<NotificationModel> sortedModels;
    private ObservableList<NotificationModel> allModels = FXCollections.observableArrayList();
    private ObservableList<NotificationModel> modelsPerCurrentPage = FXCollections.observableArrayList();

    private static final int itemsPerPageCount = 2;



    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @FXML
    Pagination notificationPagination;

    void init(){
        //models = getNotificationsModels();
        allModels.addAll(getNotificationsModels());
        Comparator<NotificationModel> cmp = (m1, m2) -> m2.getDate().compareTo(m1.getDate());
        sortedModels = new SortedList<NotificationModel>(allModels, cmp);

        notificationPagination.setPageCount(calculateNumberOfPages(sortedModels.size(),
                itemsPerPageCount));
        notificationPagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex){
        ListView<NotificationModel> view = new ListView<>();
        view.setCellFactory(list -> new ListCell<NotificationModel>(){
            @Override
            protected void updateItem(NotificationModel item, boolean empty){
                super.updateItem(item, empty);
                if(item == null || empty){
                    setText(null);
                }else{
                    Node view = item.getViewForModel();
                    setGraphic(view);
                }
            }
        });
        setModelsOnPage(pageIndex);
        view.setItems(modelsPerCurrentPage);
        return view;
    }

    private void setModelsOnPage(int pageIndex){
        int start = pageIndex * itemsPerPageCount;
        int end = Math.min((pageIndex + 1) * itemsPerPageCount, sortedModels.size());
        modelsPerCurrentPage.setAll(sortedModels.subList(start, end));
    }

    public List<NotificationModel> getNotificationsModels() {
        List<NotificationModel> models = new ArrayList<>();
        for(var dto : service.getAllFriendRequestsSentToUser(loggedUser.getId())){
            NotificationModel model;
            Status status = dto.getFriendRequest().getStatus();
            if (status == Status.PENDING){
                model = new PendingFriendRequestReceivedByUserModel(dto,
                        this::handleAcceptFriendRequest,
                        this::handleDeclineFriendRequest);
                model.setDate(dto.getFriendRequest().getDate());
            } else if(status == Status.APPROVED){
                model = new ApprovedFriendRequestReceivedByUserModel(dto);
                LocalDateTime date = service.findDateOfFriendship(loggedUser.getId(), dto.getSender().getId());
                model.setDate(date);
            } else {
                model = new RejectedFriendRequestReceivedByUserModel(dto);
                model.setDate(dto.getFriendRequest().getDate());
            }
            models.add(model);
        }

        for(var dto : service.getAllFriendRequestsSentByUser(loggedUser.getId())){
            Status status = dto.getFriendRequest().getStatus();

            if (status == Status.APPROVED){
                var model = new ApprovedFriendRequestSentByUserModel(dto);
                LocalDateTime date = service.findDateOfFriendship(dto.getSender().getId(), dto.getReceiver().getId());
                model.setDate(date);
                models.add(model);
            }
        }

        for(var event: service.getAllEventsThatAreCloseToCurrentDateForUser(loggedUser.getId())){
            var model = new EventModel(event);
            model.setDate(LocalDateTime.now());
            models.add(model);
        }

        return models;
    }

    public void handleAcceptFriendRequest(ActionEvent event){
        Button btn = (Button) event.getSource();
        User sender = (User)btn.getUserData();
        System.out.println("accept" + "  " + sender);
        service.acceptOrRejectFriendRequestService(loggedUser.getId(), sender.getId(), Status.APPROVED);
        allModels.removeIf(model -> isNotificationPendingFriendRequestBetween(model,
                sender.getId(),
                loggedUser.getId() ));
        var dto = service.findFriendRequestDto(sender.getId(), loggedUser.getId());
        var model = new ApprovedFriendRequestReceivedByUserModel(dto);
        model.setDate(LocalDateTime.now());
        allModels.add(model);
        setModelsOnPage(notificationPagination.getCurrentPageIndex());
    }

    private boolean isNotificationPendingFriendRequestBetween(NotificationModel model, Long senderId, Long receiverId){
        if (model instanceof PendingFriendRequestReceivedByUserModel) {
            var pending = (PendingFriendRequestReceivedByUserModel)model;
            var requestId = pending.getFriendRequestDto().getFriendRequest().getId();
            return requestId.equals(new UnorderedPair<>(senderId, receiverId));
        }
        return false;
    }

    public void handleDeclineFriendRequest(ActionEvent event){
        Button btn = (Button) event.getSource();
        User sender = (User)btn.getUserData();
        service.acceptOrRejectFriendRequestService(loggedUser.getId(), sender.getId(), Status.REJECTED);
        allModels.removeIf(model -> isNotificationPendingFriendRequestBetween(model,
                sender.getId(),
                loggedUser.getId() ));

        var dto = service.findFriendRequestDto(sender.getId(), loggedUser.getId());
        var model = new RejectedFriendRequestReceivedByUserModel(dto);
        model.setDate(LocalDateTime.now());
        allModels.add(model);
        System.out.println("decline" + "  " + sender);
        setModelsOnPage(notificationPagination.getCurrentPageIndex());
    }

    private int calculateNumberOfPages(int itemsNumber, int itemsPerPageNumber) {
        int numberOfPages = 0;
        if(itemsNumber < itemsPerPageNumber) {
            numberOfPages = 1;
        }
        else {
            numberOfPages = itemsNumber / itemsPerPageNumber;
            if(itemsNumber % itemsPerPageNumber != 0)
                numberOfPages++;
        }
        return numberOfPages;
    }
}
