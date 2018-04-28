package com.handwheel.service;

import com.handwheel.model.Contact;
import com.handwheel.model.Message;
import com.handwheel.model.User;


public interface UserService {
    void sendFriendRequest(String name);
    void cancelRequest(String name);
    void allowFriendRequest(String name);
    void denyFriendRequest(String name);
    void deleteFriend(Contact contact);
    void sendMessageTo(Contact contact, String message);
    void sendMessage(Message message);
    void startDialog(Contact contact);
    void deleteDialog(Contact contact);
    void addOnError(Runnable callback);
    boolean removeOnError(Runnable callback);
    User getUser();
    User newUser(String username);
}
