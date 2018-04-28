package com.handwheel.ui;

import com.handwheel.model.Contact;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ContactPane extends Pane {
    private Contact contact;
    private Rectangle shape;

    public ContactPane(Contact contact) {
        this.contact = contact;
        Text name = new Text(contact.getName()),
                status = new Text(contact.getStatus() ? "online" : "offline");
        name.setX(5);
        name.setY(15);
        name.setFont(new Font(20));
        shape = new Rectangle();
        shape.setArcHeight(15);
        shape.setArcWidth(15);
        setShape(shape);
        status.setX(5);
        status.setY(30);
        getChildren().addAll(name, status);
    }

    @Override
    public void setPrefSize(double prefWidth, double prefHeight) {
        shape.setWidth(prefWidth);
        shape.setHeight(prefHeight);
        super.setPrefSize(prefWidth, prefHeight);
    }

    public Contact getContact() {
        return contact;
    }
}
