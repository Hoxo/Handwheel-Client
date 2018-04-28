package com.handwheel.model;

import java.time.LocalDateTime;
import java.util.*;

public class User {
    private String name;
    private LocalDateTime accessDate;
    private List<Contact> contactList, incomingRequests, outgoingRequests;
    private Map<Contact, Dialog> dialogs;
    private Runnable onNewMessage, onContactUpdate, onInReqUpdate, onOutReqUpdate;

    public User(String name) {
        this.name = name;
        accessDate = LocalDateTime.now();
        contactList = new LinkedList<>();
        incomingRequests = new LinkedList<>();
        outgoingRequests = new LinkedList<>();
        dialogs = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getAccessDate() {
        return accessDate;
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public List<Contact> getIncomingRequests() {
        return incomingRequests;
    }

    public List<Contact> getOutgoingRequests() {
        return outgoingRequests;
    }

    public Dialog getDialog(Contact contact) {
        return dialogs.get(contact);
    }

    public Map<Contact, Dialog> getDialogs() {
        return dialogs;
    }

    public void setOnNewMessage(Runnable runnable) {
        onNewMessage = runnable;
    }

    public Runnable onNewMessage() {
        return onNewMessage;
    }

    public void removeOnNewMessage() {
        onNewMessage = null;
    }

    public void newMessage() {
        if (onNewMessage != null)
            onNewMessage.run();
    }

    public Dialog addDialog(Contact contact) {
        Dialog dialog = new Dialog(contact);
        dialogs.put(contact, dialog);
        return dialog;
    }

    public boolean hasDialog(Contact contact) {
        return dialogs.containsKey(contact);
    }

    public Contact getContactByName(String name) {
        for (Contact contact : contactList) {
            if (contact.getName().equals(name))
                return contact;
        }
        return null;
    }

    public void removeDialogWith(Contact contact) {
        dialogs.remove(contact);
    }

    public void removeDialog(Dialog dialog) {
        dialogs.remove(dialog.getContact());
    }

    public void removeContact(Contact contact) {
        contactList.remove(contact);
        dialogs.remove(contact);
    }

    public void addNewMessage(Contact contact, Message message) {
        dialogs.get(contact).addNewMessage(message);
        newMessage();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateContacts(Collection<Contact> contacts) {
        this.contactList = new LinkedList<>(contacts);
        if (onContactUpdate != null)
            onContactUpdate.run();
    }

    public void updateIncomingRequests(Collection<Contact> requests) {
        incomingRequests = new LinkedList<>(requests);
        if (onInReqUpdate != null)
            onInReqUpdate.run();
    }

    public void updateOutgoingRequests(Collection<Contact> requests) {
        outgoingRequests = new LinkedList<>(requests);
        if (onOutReqUpdate != null)
            onOutReqUpdate.run();
    }

    public void addIncomingRequest(Contact from) {
        incomingRequests.add(from);
        if (onInReqUpdate != null)
            onInReqUpdate.run();
    }

    public void addOutgoingRequest(Contact to) {
        outgoingRequests.add(to);
        if (onOutReqUpdate != null)
            onOutReqUpdate.run();
    }


    public void setOnContactUpdate(Runnable callback) {
        onContactUpdate = callback;
    }

    public void setOnIncomingRequestUpdate(Runnable callback) {
        onInReqUpdate = callback;
    }

    public void setOnOutgoingRequestUpdate(Runnable callback) {
        onOutReqUpdate = callback;
    }

}
