package com.handwheel;

import com.alibaba.fastjson.JSONObject;
import com.handwheel.converter.JSONConverter;
import com.handwheel.factory.*;
import com.handwheel.model.Message;
import com.handwheel.parser.Parser;
import com.handwheel.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private MainControllerInterface mainController;
    private MessengerService messengerService;
    private Properties prop;
    private String address = "ws://localhost:6557";

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    static {
        LOGGER.getParent().setLevel(Level.OFF);
        LOGGER.getParent().getHandlers()[0].setLevel(Level.OFF);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        LOGGER.addHandler(consoleHandler);
        LOGGER.setLevel(Level.ALL);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setConfigurations();
        Parser parser = new Parser() {
            @Override
            public Map<String, Object> parse(String message) {
                return JSONObject.parseObject(message);
            }

            @Override
            public Message parse(Map<String, Object> map) {
                return null;
            }
        };

        File file = new File("client.jar");
        ClientServiceFactory clientFactory = new URLClientServiceFactory(file.toURI().toURL(),
                prop.getProperty("user_service"), prop.getProperty("authorization_service"));

        MessengerServiceFactory messengerFactory = getMessengerServiceFactory();
        messengerService = messengerFactory.createMessengerService(address, parser, new JSONConverter());
        messengerService.start();
        UserService userService = clientFactory.createUserService(messengerService);
        AuthorizationService as = clientFactory.createAuthorizationService(messengerService, userService);

        FXMLLoader loader = new FXMLLoader();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("TextResource");
        loader.setResources(resourceBundle);
        loader.setLocation(getClass().getResource("MainFrame.fxml"));
        Parent fxroot = loader.load();
        primaryStage.setScene(new Scene(fxroot));
        primaryStage.setTitle("Handwheel");
        mainController = loader.getController();
        mainController.setStage(primaryStage);
        mainController.setUserService(userService);
        mainController.setAuthorizationService(as);
        mainController.initBindings();

        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void setConfigurations() {
        try (FileReader reader = new FileReader("config.properties")) {

            prop = new Properties();
            prop.load(reader);
            address = prop.getProperty("address");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClientServiceFactory getClientServiceFactory() {
        return new HandwheelServiceFactory();
    }

    private MessengerServiceFactory getMessengerServiceFactory() {
        return new WebSocketServiceFactory();
    }

    public void stop() {
        messengerService.stop();
    }
}