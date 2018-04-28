package com.handwheel.service;

import com.handwheel.converter.Converter;
import com.handwheel.parser.Parser;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;

public interface ConnectionService {
    void connect(String rout) throws ConnectException;
    boolean isConnected();
    void setAddress(String address);
    String getAddress();
    void disconnect();
    void send(String text) throws IOException;
    void start();
    void stop();
    void addOnDisconnect(Runnable runnable);
    boolean removeOnDisconnect(Runnable runnable);
    Parser getParser();
    Converter getConverter();
}
