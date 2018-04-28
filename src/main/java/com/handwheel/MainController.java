package com.handwheel;

import com.handwheel.model.Dialog;
import com.handwheel.model.Contact;
import com.handwheel.model.Message;
import com.handwheel.model.User;
import com.handwheel.service.AuthorizationService;
import com.handwheel.service.UserService;
import com.handwheel.ui.ContactPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainController implements MainControllerInterface {
    @FXML
    private ListView<Contact> friendsListView;
    @FXML
    private ListView<Contact> incomingListView;
    @FXML
    private ListView<Contact> outgoingListView;
    @FXML
    private TextArea messageArea;
    @FXML
    private ScrollPane messagePane;
    @FXML
    private VBox messageBox;
    @FXML
    private ListView<Dialog> dialogs;
    @FXML
    private TextField destination;
    @FXML
    private MenuItem connect;

    private Stage mainStage, loginStage;
    private AuthorizationService authorizationService;
    private LoginControllerInterface loginWindowController;
    private UserService userService;


    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @Override
    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void initialize() {

    }

    public void initUserBindings() {
        User user = userService.getUser();
        user.setOnContactUpdate(() -> {
            Platform.runLater(() -> {
                friendsListView.getItems().setAll(user.getContactList());
            });
        });
        user.setOnIncomingRequestUpdate(() -> {
            Platform.runLater(() -> {
                incomingListView.getItems().setAll(user.getIncomingRequests());
            });
        });
        user.setOnOutgoingRequestUpdate(() -> {
            Platform.runLater(() -> {
                outgoingListView.getItems().setAll(user.getOutgoingRequests());
            });
        });
        Platform.runLater(() -> {
            userService.getUser().setOnNewMessage(() -> {
                Platform.runLater(() -> {
//                    dialogs.refresh();
//                    if (dialogs.getSelectionModel().getSelectedItem() == null)
//                        return;
//                    Dialog dialog = dialogs.getSelectionModel().getSelectedItem();
//                    if (!dialog.getNewMessages().isEmpty())
//                        showAndSaveNewMessages(dialog);
                });
            });
        });
    }

    @Override
    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initBindings() {
        authorizationService.addOnSignInSuccess(() -> {
            Platform.runLater(() -> {
                connect.setDisable(true);
                mainStage.setTitle("Handwheel - Вы авторизированы под именем: " + userService.getUser().getName());
                messageBox.getChildren().clear();
                initUserBindings();
            });
        });

        authorizationService.addOnDisconnect(() -> {
            Platform.runLater(() -> {
                mainStage.setTitle("Handwheel - Подключение к сети отсутствует");
                connect.setDisable(false);
                friendsListView.getItems().clear();
                incomingListView.getItems().clear();
                outgoingListView.getItems().clear();
                dialogs.getItems().clear();
                messageBox.getChildren().clear();
            });
        });
        dialogs.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if(dialogs.getSelectionModel().getSelectedItem() != null)
                showMessages();
            else
                messageBox.getChildren().clear();
            dialogs.refresh();
        });
    }

    private void initLoginWindow() throws Exception {
        loginStage = new Stage();
        loginStage.initModality(Modality.WINDOW_MODAL);
        loginStage.initOwner(mainStage);
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("TextResource"));
        loader.setLocation(getClass().getResource("LoginFrame.fxml"));
        Parent fxroot = loader.load();
        loginStage.setScene(new Scene(fxroot));

        loginWindowController = loader.getController();
        loginWindowController.setAuthorizationService(authorizationService);
        loginWindowController.setStage(loginStage);

        loginStage.sizeToScene();
        loginStage.setResizable(false);
    }

    private void showWindow() throws Exception {
        if (loginStage == null)
            initLoginWindow();
        loginStage.show();
    }

    @FXML
    public void showLoginWindow() {
        try {
            showWindow();
        } catch (Exception e) {
            LOGGER.warning("Login Window wasn't shown");
        }
    }

    @FXML
    public void handleDisconnect() {
        authorizationService.logOut();
    }

    @FXML
    public void handleClose() {
        authorizationService.logOut();
        mainStage.close();
    }

    @FXML
    public void handleDeleteFriend() {
        if (!friendsListView.getSelectionModel().isEmpty()) {
            Contact contact = friendsListView.getSelectionModel().getSelectedItem();
            userService.deleteFriend(contact);
        }
    }

    @FXML
    private void sendMessage(TextArea from, ScrollPane scroll, VBox box) {
        if (authorizationService.isOnline() && !dialogs.getSelectionModel().isEmpty()) {
            Dialog dialog = dialogs.getSelectionModel().getSelectedItem();
            Message message = Message.createMessage(userService.getUser().getName(),from.getText(), dialog.getContactName());
            try {
                userService.sendMessage(message);
                showMessage(message,scroll,box);
                from.clear();
                dialog.getReceivedMessages().add(message);
            } catch (Exception e) {
                LOGGER.warning("Message didn't send");
                e.printStackTrace();
                showSysMessage("Error! Message didn't send",scroll,box);
            }
        }
    }

    @FXML
    public void handleCancelRequest() {
        if (!outgoingListView.getSelectionModel().isEmpty())
            userService.cancelRequest(outgoingListView.getSelectionModel().getSelectedItem().getName());
    }

    @FXML
    public void handleStartDialog() {
        if (!friendsListView.getSelectionModel().isEmpty()) {
            Contact contact = friendsListView.getSelectionModel().getSelectedItem();
            userService.startDialog(contact);
            Dialog temp = userService.getUser().getDialog(contact);
            dialogs.getSelectionModel().select(temp);
        }
    }

    private void showReceivedMessages(Dialog dialog) {
        for (Message message: dialog.getReceivedMessages())
            showMessage(message,messagePane,messageBox);
    }

    private void showAndSaveNewMessages(Dialog dialog)
    {
        if (!dialog.getNewMessages().isEmpty())
            while (!dialog.getNewMessages().isEmpty()) {
                showMessage(dialog.getNewMessages().get(0), messagePane, messageBox);
                dialog
                        .getReceivedMessages()
                        .add(dialog
                                .getNewMessages()
                                .get(0));
                dialog
                        .getNewMessages()
                        .remove(0);
            }
    }

    private void showMessages()
    {
        messageBox.getChildren().clear();
        Dialog from = dialogs.getSelectionModel().getSelectedItem();
        showReceivedMessages(from);
        showAndSaveNewMessages(from);
    }

    @FXML
    public void handleAllowRequest() {
        if (!incomingListView.getSelectionModel().isEmpty())
            userService.allowFriendRequest(incomingListView.getSelectionModel().getSelectedItem().getName());
    }

    @FXML
    public void handleDenyRequest() {
        if (!incomingListView.getSelectionModel().isEmpty())
            userService.denyFriendRequest(incomingListView.getSelectionModel().getSelectedItem().getName());
    }

    @FXML
    public void handleAddFriend() {
        if (!destination.getText().isEmpty() && authorizationService.isOnline()
                && !destination.getText().equals(userService.getUser().getName())) {
            userService.sendFriendRequest(destination.getText());
            destination.clear();
        }
    }

    private void showMessage(Message message, ScrollPane pane, VBox box)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/YY");
        Text time = new Text("<" + sdf.format(message.getDate()) + ">");

        Text sender = new Text(message.getSender() + ":");
        sender.setStyle("-fx-font-weight: bold");
        if (message.isIncoming())
            sender.setFill(Color.RED);
        else
            sender.setFill(Color.BLUE);
        Text messageView = new Text(message.getText());
        messageView.setWrappingWidth(messagePane.getWidth()-15);
        box.getChildren().addAll(new HBox(0, time, sender), messageView);
        pane.setVvalue(10);
    }

    private void showSysMessage(String sys, ScrollPane pane, VBox box)
    {
        Text messageView = new Text(sys);
        messageView.setFill(Color.GREEN);
        messageView.setWrappingWidth(pane.getWidth()-15);
        box.getChildren().add(messageView);
        pane.setVvalue(1);
    }

    @FXML
    public void handleDeleteDialog()
    {
        if(!dialogs.getSelectionModel().isEmpty())
            userService.deleteDialog(dialogs.getSelectionModel().getSelectedItem().getContact());
        messageBox.getChildren().clear();
    }

    @FXML
    public void handleSend() {
//        sendMessage(messageArea,messagePane,messageBox);
        messageBox.setSpacing(2);
        Pane wrapper = new Pane();
        ContactPane pane = new ContactPane(new Contact("Vasya", true));
        pane.setPrefSize(100, 50);
        wrapper.setPrefWidth(messagePane.getWidth());
        wrapper.setStyle("-fx-background-color: #000000");
        wrapper.setPadding(new Insets(5,5,5,5));
        wrapper.getChildren().add(pane);
        pane.setStyle("-fx-background-color: #fcabdd");
        pane.setOnMouseClicked(event -> System.out.println(pane.getContact()));
        wrapper.setOnMouseClicked(event -> System.out.println("hello there"));
        messageBox.getChildren().add(wrapper);
    }

}
