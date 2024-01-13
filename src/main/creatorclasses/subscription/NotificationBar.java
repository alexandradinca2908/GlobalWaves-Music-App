package main.creatorclasses.subscription;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public final class NotificationBar {
    private String subscriber;
    private ArrayList<String> notifications = new ArrayList<>();

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<String> notifications) {
        this.notifications = notifications;
    }
}
