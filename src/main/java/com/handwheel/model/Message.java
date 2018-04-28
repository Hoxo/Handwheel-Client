package com.handwheel.model;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class Message implements Comparable{
    private String sender;
    private String message;
    private LocalDateTime date;
    private boolean isIncoming;
    private List<String> destination;

    public Message(String sender, String message, LocalDateTime date, List<String> dest) {
        this.sender = sender;
        this.message = message;
        this.isIncoming = false;
        this.date = date;
        destination = new LinkedList<>(dest);
    }

    public static Message createMessage(String from, String text, String to, String... other) {
        String[] a = new String[other.length + 1];
        System.arraycopy(other, 0, a, 1, other.length);
        a[0] = to;
        return new Message(from, text, LocalDateTime.now() ,Arrays.asList(a));
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public List<String> getDestination() {
        return destination;
    }

    @Override
    public int compareTo(Object o) {
        return date.compareTo(((Message)o).date);
    }

}
