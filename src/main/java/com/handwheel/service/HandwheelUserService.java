package com.handwheel.service;

import com.handwheel.converter.Converter;
import com.handwheel.model.*;
import com.handwheel.parser.Parser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class HandwheelUserService implements UserService {

    private User user;
    private MessengerService messengerService;
    private List<Runnable> onError;

    public HandwheelUserService(MessengerService messengerService) {
        this.messengerService = messengerService;
        onError = new LinkedList<>();
        messengerService.addHandler(map -> {
            String key = "friends";
            if (map.containsKey(key)) {
                Map<String, Object> f = (Map<String, Object>) map.get(key);
                List<Contact> contacts = new LinkedList<>();
                for (Map.Entry<String, Object> entry : f.entrySet()) {
                    contacts.add(new Contact(entry.getKey(), (boolean)entry.getValue()));
                }
                user.updateContacts(contacts);
            }
        });
        messengerService.addHandler(map -> {
            String key = "income";
            if (map.containsKey(key)) {
                Map<String, Object> f = (Map<String, Object>) map.get(key);
                List<Contact> incoming = new LinkedList<>();
                for (Map.Entry<String, Object> entry : f.entrySet()) {
                    incoming.add(new Contact(entry.getKey(), (boolean)entry.getValue()));
                }
                user.updateIncomingRequests(incoming);
            }
        });
        messengerService.addHandler(map -> {
            String key = "outgoing";
            if (map.containsKey(key)) {
                Map<String, Object> f = (Map<String, Object>) map.get(key);
                List<Contact> outgoing = new LinkedList<>();
                for (Map.Entry<String, Object> entry : f.entrySet()) {
                    outgoing.add(new Contact(entry.getKey(), (boolean)entry.getValue()));
                }
                user.updateOutgoingRequests(outgoing);
            }
        });
        messengerService.addHandler(map -> {
            if (map.containsKey("msg")) {
                Map<String, Object> msg = (Map<String, Object>) map.get("msg");
                Message message = messengerService.getParser().parse(msg);
                Contact sender = user.getContactByName(message.getSender());
                user.addNewMessage(sender, message);
            }
        });
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void sendFriendRequest(String name) {
        sendCustomRequest("outgoing", name);
    }

    @Override
    public void cancelRequest(String name) {
        sendCustomRequest("cancel",name);
    }

    @Override
    public User newUser(String username) {
        user = new User(username);
        return user;
    }

    @Override
    public void allowFriendRequest(String name){
        sendCustomRequest("allow",name);
    }

    @Override
    public void denyFriendRequest(String name){
        sendCustomRequest("denied",name);
    }

    @Override
    public void deleteFriend(Contact contact) {
        sendCustomRequest("delfriend", contact.getName());
    }

    private void sendCustomRequest(String key, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(key,value);
        try {
            messengerService.send(messengerService.getConverter().convert(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startDialog(Contact contact) {
        if (user.hasDialog(contact))
            user.addDialog(contact);
    }

    @Override
    public void deleteDialog(Contact contact) {
        user.removeDialogWith(contact);
    }

    @Override
    public void sendMessage(Message message) {
        try {
            messengerService.send(messengerService.getConverter().convert(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageTo(Contact contact, String message) {
        Message m = new Message(user.getName(), message, LocalDateTime.now(), Arrays.asList(contact.getName()));
        try {
            messengerService.send(messengerService.getConverter().convert(m));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOnError(Runnable callback) {
        onError.add(callback);
    }

    @Override
    public boolean removeOnError(Runnable callback) {
        return onError.remove(callback);
    }
}
