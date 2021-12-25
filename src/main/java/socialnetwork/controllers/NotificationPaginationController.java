package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.util.Callback;
import socialnetwork.domain.entities.Status;
import socialnetwork.domain.entities.User;
import socialnetwork.models.*;
import socialnetwork.service.SocialNetworkService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationPaginationController {
    private SocialNetworkService service;
    private User loggedUser;
    private List<NotificationModel> models;
    private ObservableList<NotificationModel> pageObsList;
    private ListView<NotificationModel> mainView = new ListView<>();
    private static final int itemsPerPageCount = 4;



    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @FXML
    Pagination notificationPagination;

    void init(){
        models = getNotificationsModels();
        notificationPagination.setPageCount(calculateNumberOfPages(models.size(),
                itemsPerPageCount));
        notificationPagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
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
                int start = pageIndex * itemsPerPageCount;
                int end = Math.min((pageIndex + 1) * itemsPerPageCount, models.size());
                pageObsList = FXCollections.observableArrayList(models.subList(start, end));
                view.setItems(pageObsList);
                return view;
            }
        });

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
        return models.stream().sorted((m1, m2) -> m2.getDate().compareTo(m1.getDate()))
                .collect(Collectors.toList());
    }

    public void refreshObservableList(){
        models = getNotificationsModels();
        int pageIndex = notificationPagination.getCurrentPageIndex();
        int start = pageIndex * itemsPerPageCount;
        int end = Math.min((pageIndex + 1) * itemsPerPageCount, models.size());
        pageObsList.setAll(models.subList(start, end));
    }

    public void handleAcceptFriendRequest(ActionEvent event){
        Button btn = (Button) event.getSource();
        User sender = (User)btn.getUserData();
        System.out.println("accept" + "  " + sender);
        service.acceptOrRejectFriendRequestService(loggedUser.getId(), sender.getId(), Status.APPROVED);
        refreshObservableList();

    }

    public void handleDeclineFriendRequest(ActionEvent event){
        Button btn = (Button) event.getSource();
        User sender = (User)btn.getUserData();
        service.acceptOrRejectFriendRequestService(loggedUser.getId(), sender.getId(), Status.REJECTED);
        refreshObservableList();
        System.out.println("decline" + "  " + sender);
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
