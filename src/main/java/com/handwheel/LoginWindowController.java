package com.handwheel;

import com.handwheel.service.AuthorizationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class LoginWindowController implements LoginControllerInterface {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private Stage loginStage;
    private AuthorizationService authorizationService;
    private ResourceBundle bundle;

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    public LoginWindowController()
    {
        bundle = ResourceBundle.getBundle("ServerMessageResource");
//        try {
//            File file = new File(getClass().getResource("responses.json").toURI());
//            jsonMap = parseJSONFile(file);
//        }
//        catch (Exception e) {
//            LOGGER.warning(e.toString());
//        }
    }

    @Override
    public void setStage(Stage stage) {
        this.loginStage = stage;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
        authorizationService.addOnServerResponse(response -> {
            Platform.runLater( () -> handleServerResponse(response));
        });
        authorizationService.addOnSignInSuccess(() -> {
            Platform.runLater(() -> loginStage.close());
        });
    }

    @FXML
    public void handleSignIn() {
        try {
            authorizationService.signIn(usernameField.getText(),passwordField.getText());
        } catch (Exception e) {
            setBadConnection(e);
        }
    }

    @FXML
    public void handleSignUp() {
        try {
            authorizationService.signUp(usernameField.getText(), passwordField.getText());
        } catch (ConnectException e) {
            setBadConnection(e);
        }
    }

    @FXML
    public void handleClear() {
        usernameField.clear();
        passwordField.clear();
    }

    public void handleServerResponse(String message) {
        updateInfo(makeKeyForResource(message));
    }

    private String makeKeyForResource(String response) {
        return response.trim().replace(' ','_');
    }

    private void setBadConnection(Exception e) {
        updateInfo(makeKeyForResource("server_is_not_responding"));
        LOGGER.warning("Connection error - " + e.getMessage());
    }

    private void updateInfo(String key) {
        String msg = bundle.getString(key);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}
