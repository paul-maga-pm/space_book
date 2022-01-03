package socialnetwork.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import socialnetwork.Run;
import socialnetwork.domain.entities.Event;
import socialnetwork.domain.entities.EventParticipant;
import socialnetwork.domain.entities.NotificationStatus;
import socialnetwork.domain.entities.User;
import socialnetwork.exceptions.ExceptionBaseClass;
import socialnetwork.service.SocialNetworkService;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventsPageController {
    private SocialNetworkService service;
    private User loggedUser;

    private List<Event> events = new ArrayList<>();
    private List<ToggleButton> signUpToggleButtons = new ArrayList<>();

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public User getLoggedUser(){
        return loggedUser;
    }

    public void setService(SocialNetworkService service) {
        this.service = service;
        setPaginationModel();
    }

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button fileChooserButton;
    @FXML
    private FileChooser fileChooser = new FileChooser();
    @FXML
    Pagination pagination;

    private File imageFile;

    @FXML
    public void initialize(){
        datePicker.setValue(LocalDate.now());
        StringConverter stringConverter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if(date != null)
                    return dateFormatter.format(date);
                else
                    return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if(string != null && !string.isEmpty())
                    return LocalDate.parse(string, dateFormatter);
                else
                    return null;
            }
        };
        datePicker.setConverter(stringConverter);
        datePicker.setPromptText("yyyy-mm-dd");

        fileChooserButton.setOnAction((ActionEvent event) -> {
            File file = fileChooser.showOpenDialog(Run.getPrimaryStage());
            if(file != null){
                imageFile = file;
                fileChooserButton.setText("Image chosen!");
            }
        });
    }

    @FXML
    public void handleClickOnAddEventButton(ActionEvent event){
        if(imageFile == null){
            Run.showPopUpWindow("Warning", "Must choose an image file!");
            return;
        }

        String name = nameTextField.getText();
        String description = descriptionTextField.getText();
        LocalDate date = datePicker.getValue();
        try{
            service.addEventService(name, description, date, imageFile.getAbsolutePath());

            setPaginationModel();
            pagination.setCurrentPageIndex(pagination.getPageCount()-1);

            nameTextField.clear();
            descriptionTextField.clear();
            datePicker.setValue(LocalDate.now());
            imageFile = null;
            fileChooserButton.setText("Choose an image!");
        }catch (ExceptionBaseClass exception) {
            Run.showPopUpWindow("Warning", exception.getMessage());
        }
    }

    private void handleClickOnSignUpToEventToggleButton(ActionEvent event, Long eventId, ToggleButton signUp){
        Optional<EventParticipant> eventParticipant = service.findOneEventParticipantService(loggedUser.getId(), eventId);
        if(eventParticipant.isPresent()){
            service.removeEventParticipantService(loggedUser.getId(), eventId);
            signUp.setSelected(false);
            signUp.setText("Sign Up");
        }
        else{
            service.addEventParticipantService(loggedUser.getId(), eventId, NotificationStatus.SUBSCRIBED);
            signUp.setSelected(true);
            signUp.setText("Signed Up");
        }
    }

    private void setSignUpToggleButtonState(ToggleButton signUp, Event event){
        if(ChronoUnit.DAYS.between(LocalDate.now(), event.getDate()) < 0){
            signUp.setText("Sign Up");
            signUp.setDisable(true);
            return;
        }
        Optional<EventParticipant> eventParticipant = service.findOneEventParticipantService(loggedUser.getId(), event.getId());
        if(eventParticipant.isPresent()){
            signUp.setSelected(true);
            signUp.setText("Signed Up");
        }
        else{
            signUp.setSelected(false);
            signUp.setText("Sign Up");
        }
    }

    private HBox createPage(int pageIndex){
        Event event = events.get(pageIndex);

        VBox vBox = new VBox();
        Label nameLabel = new Label(event.getName());
        Label descriptionLabel = new Label(event.getDescription());
        Label dateLabel = new Label(event.getDate().toString());
        ToggleButton signUp = new ToggleButton();
        setSignUpToggleButtonState(signUp, event);
        signUp.setOnAction((ActionEvent e) -> handleClickOnSignUpToEventToggleButton(e, event.getId(), signUp));
        signUpToggleButtons.add(signUp);
        vBox.getChildren().addAll(nameLabel, descriptionLabel, dateLabel, signUp);

        //String imageFile = event.getImageFile();
        //int index = imageFile.lastIndexOf('\\');
        //imageFile = imageFile.substring(index+1);
        //Image eventImage = new Image(String.valueOf(Run.class.getResource(imageFile)));
        Image eventImage = new Image(String.valueOf(Run.class.getResource("rick.jpg")));
        ImageView imageView = new ImageView(eventImage);
        imageView.setFitWidth(250);
        imageView.setPreserveRatio(true);
        HBox hBox = new HBox(imageView, vBox);

        return hBox;
    }

    private void setPaginationModel(){
        signUpToggleButtons.removeAll(signUpToggleButtons);
        events = service.getAllEventsService();
        pagination.setPageCount(events.size());
        pagination.setPageFactory((Integer pageIndex) -> {
            if(pageIndex >= events.size())
                return null;
            else
                return createPage(pageIndex);
        });
    }
}
