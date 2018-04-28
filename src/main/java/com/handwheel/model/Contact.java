package com.handwheel.model;

import java.util.Objects;

public class Contact implements Comparable{
    private String name;
    private boolean status;

    public Contact(String name, boolean isOnline) {
        this.name = name;
        this.status = isOnline;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static Contact searchTemplate(String name)
    {
        return new Contact(name,false);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (" + (status?"Online":"Offline") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return name.equals(contact.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((Contact)o).getName());
    }
}
