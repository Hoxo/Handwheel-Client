package com.handwheel.model;

import java.util.LinkedList;
import java.util.List;

public class Dialog {
    private Contact contact;
    private List<Message> receivedMessages;
    private List<Message> newMessages;

    public Dialog(Contact contact) {
        this.contact = contact;
        receivedMessages = new LinkedList<>();
        newMessages = new LinkedList<>();
    }

    public String getContactName() {
        return contact.getName();
    }

    public Contact getContact() {
        return contact;
    }

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public List<Message> getNewMessages() {
        return newMessages;
    }

    public void moveNewMessagesToReceived() {
        receivedMessages.addAll(newMessages);
        newMessages.clear();
    }

    public void addNewMessage(Message message) {
        newMessages.add(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dialog dialog = (Dialog) o;
        return contact.equals(dialog.contact);
    }

    public String toString()
    {
        return contact + " [" + newMessages.size() + "]";
    }
}
