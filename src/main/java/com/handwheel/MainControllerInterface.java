package com.handwheel;

import com.handwheel.service.AuthorizationService;
import com.handwheel.service.MessengerService;
import com.handwheel.service.UserService;
import javafx.stage.Stage;

public interface MainControllerInterface {
    void setStage(Stage stage);
    void setAuthorizationService(AuthorizationService authorizationService);
    void setUserService(UserService userService);
    void initBindings();
    void handleSend();
    void showLoginWindow();
    void handleDisconnect();
    void handleClose();
    void handleAllowRequest();
    void handleDenyRequest();
    void handleCancelRequest();
    void handleAddFriend();
    void handleDeleteFriend();
    void handleStartDialog();
    void handleDeleteDialog();
}
