package com.handwheel;

import com.handwheel.service.AuthorizationService;
import com.handwheel.service.MessengerService;
import javafx.stage.Stage;

public interface LoginControllerInterface {
    void setAuthorizationService(AuthorizationService service);
    void setStage(Stage stage);
    void handleSignIn();
    void handleSignUp();
    void handleClear();
}
