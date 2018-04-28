package com.handwheel.service;

import com.handwheel.converter.Converter;
import com.handwheel.parser.Parser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketMessengerService extends MessengerServiceBase {

    private static final long   CONNECT_TIMEOUT = 1000;
    private static final long   MAX_IDLE_TIMEOUT = 1000000;
    private static final Logger LOGGER = Logger.getLogger(WebSocketMessengerService.class.getName());

    private Session session;
    private WebSocketClient client;
    private ExecutorService executor = Executors.newScheduledThreadPool(1);
    private Parser parser;
    private Converter converter;

    public WebSocketMessengerService(String address, Parser parser, Converter converter) {
        super(address);
        LOGGER.setLevel(Level.INFO);
        LOGGER.addHandler(new ConsoleHandler());
        this.parser = parser;
        this.converter = converter;
        initClient();
    }

    private void initClient() {
        client = new WebSocketClient();
        client.setConnectTimeout(CONNECT_TIMEOUT);
        client.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);
    }

    @Override
    public void start() {
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(String rout) throws ConnectException {
        try {
            session = client.connect(new SimpleWebSocket(), URI.create(address + rout), new ClientUpgradeRequest()).get();
        } catch (Exception e) {
            throw new ConnectException(e.getMessage());
        }
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public Converter getConverter() {
        return converter;
    }

    @Override
    protected void onDisconnect() {
        for(Runnable r : onDisconnectCallbacks)
            r.run();
    }

    @Override
    public void addDisposableHandler(MapMessageHandler mapMessageHandler) {
        addHandler(new MapMessageHandler() {
            @Override
            public void handle(Map<String, Object> map) {
                mapMessageHandler.handle(map);
                handlers.remove(this);
            }
        });
    }

    //TODO Убрать костыль
    volatile boolean connected = false;

    @Override
    public void disconnect() {
        session.close();
        while (connected);
    }

    public void send(String text) throws IOException {
        session.getRemote().sendString(text);
        LOGGER.info("Message sent: " + text);
    }

    @Override
    protected void handle(String message) {
        Map<String, Object> map = parser.parse(message);
        for (MapMessageHandler handler : handlers)
            handler.handle(map);
    }

    @Override
    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    @Override
    public void stop() {
        try {
            if (isConnected())
                disconnect();
            client.stop();
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SimpleWebSocket extends WebSocketAdapter {
        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            LOGGER.info("Connection closed. Status code: " + statusCode + ", Reason: " + reason);
            connected = false;
            onDisconnect();
        }

        @Override
        public void onWebSocketConnect(Session sess) {
            LOGGER.info("Got connection!");
            connected = true;
        }

        @Override
        public void onWebSocketText(String message) {
            LOGGER.info("Got a message: " + message);
            executor.execute(() -> handle(message));
        }
    }
}
