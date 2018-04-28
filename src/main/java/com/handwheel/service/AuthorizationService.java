package com.handwheel.service;

import java.net.ConnectException;
import java.util.function.Consumer;

public interface AuthorizationService {
    void signIn(String name, String password) throws ConnectException;
    void signUp(String name, String password) throws ConnectException;
    void logOut();
    void addOnError(Consumer<Exception>  callback);
    void addOnDisconnect(Runnable callback);
    void addOnSignInSuccess(Runnable callback);
    void addOnServerResponse(Consumer<String> callback);
    boolean removeOnServerResponse(Consumer<String> callback);
    boolean removeOnError(Consumer<Exception> callback);
    boolean removeOnDisconnect(Runnable callback);
    boolean removeOnSignInSuccess(Runnable callback);
    boolean isConnected();
    boolean isOnline();

}
