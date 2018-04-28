package com.handwheel.service;

import java.util.LinkedList;
import java.util.List;

public abstract class MessengerServiceBase implements MessengerService {

    protected String address;
    protected List<MapMessageHandler> handlers = new LinkedList<>();
    protected List<Runnable> onDisconnectCallbacks = new LinkedList<>();

    public MessengerServiceBase(String address) {
        this.address = address;
    }

    @Override
    public void addHandler(MapMessageHandler mapMessageHandler) {
        handlers.add(mapMessageHandler);
    }

    @Override
    public boolean removeHandler(MapMessageHandler mapMessageHandler) {
        return handlers.remove(mapMessageHandler);
    }

    @Override
    public void addOnDisconnect(Runnable runnable) {
        onDisconnectCallbacks.add(runnable);
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public boolean removeOnDisconnect(Runnable runnable) {
        return onDisconnectCallbacks.remove(runnable);
    }

    protected abstract void handle(String message);
    protected abstract void onDisconnect();
}
