package com.handwheel.service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HandwheelAuthorizationService implements AuthorizationService {

    private MessengerService messengerService;
    private UserService userService;
    private List<Consumer<Exception>> errorHandlers;
    private List<Runnable> onDisconnect, onSignInSuccess;
    private List<Consumer<String>> onServerResponse;
    private volatile boolean isOnline;

    public HandwheelAuthorizationService(MessengerService messengerService, UserService userService) {
        this.messengerService = messengerService;
        this.userService = userService;
        errorHandlers = new LinkedList<>();
        onDisconnect = new LinkedList<>();
        onSignInSuccess = new LinkedList<>();
        onServerResponse = new LinkedList<>();
        messengerService.addHandler(map -> {
            if (map.containsKey("sys")) {
                for (Consumer<String> consumer : onServerResponse) {
                    consumer.accept(map.get("sys").toString());
                }
            }
        });
        messengerService.addOnDisconnect(() -> {
            for (Runnable runnable : onDisconnect) {
                runnable.run();
            }
        });
    }

    @Override
    public void signIn(String name, String password) throws ConnectException {
        messengerService.addDisposableHandler(map -> {
            messengerService.disconnect();
            if (map.containsKey("sys") && map.get("sys").equals("access allow"))
                try {
                    userService.newUser(name);
                    onSignInSuccess();
                    messengerService.connect("/");
                    sendUserData(name,password);
                    isOnline = true;
                } catch (ConnectException e) {
                    handleError(e);
                }
        });
        messengerService.connect("/login");
        sendUserData(name,password);

    }

    private void onSignInSuccess() {
        for (Runnable runnable : onSignInSuccess) {
            runnable.run();
        }
    }

    @Override
    public void addOnError(Consumer<Exception> callback) {
        errorHandlers.add(callback);
    }

    @Override
    public boolean removeOnError(Consumer<Exception> callback) {
        return errorHandlers.remove(callback);
    }

    @Override
    public void signUp(String name, String password) throws ConnectException {
//        addSelfDestructingHandler((object) -> disconnect());
        messengerService.connect("/signin");
        sendUserData(name,password);
    }

    private void sendUserData(String name, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("password",password);
        try {
            messengerService.send(messengerService.getConverter().convert(map));
        } catch (IOException e) {
            handleError(e);
        }
    }

    @Override
    public void addOnServerResponse(Consumer<String> callback) {
        onServerResponse.add(callback);
    }

    @Override
    public boolean removeOnServerResponse(Consumer<String> callback) {
        return onServerResponse.remove(callback);
    }

    @Override
    public void logOut() {
        Map<String, Object> map = new HashMap<>();
        map.put("sys","close");
        try {
            messengerService.send(messengerService.getConverter().convert(map));
        } catch (IOException e) {
            handleError(e);
        }
    }

    private void handleError(Exception e) {
        for (Consumer<Exception> handler : errorHandlers) {
            handler.accept(e);
        }
    }

    @Override
    public void addOnDisconnect(Runnable callback) {
        onDisconnect.add(callback);
    }

    @Override
    public void addOnSignInSuccess(Runnable callback) {
        onSignInSuccess.add(callback);
    }

    @Override
    public boolean removeOnDisconnect(Runnable callback) {
        return onDisconnect.remove(callback);
    }

    @Override
    public boolean removeOnSignInSuccess(Runnable callback) {
        return onSignInSuccess.remove(callback);
    }

    @Override
    public boolean isConnected() {
        return messengerService.isConnected();
    }

    @Override
    public boolean isOnline() {
        return isOnline;
    }

}
