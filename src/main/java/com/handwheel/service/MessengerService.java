package com.handwheel.service;

public interface MessengerService extends ConnectionService {
    void addHandler(MapMessageHandler mapMessageHandler);
    boolean removeHandler(MapMessageHandler mapMessageHandler);
    void addDisposableHandler(MapMessageHandler mapMessageHandler);
}
