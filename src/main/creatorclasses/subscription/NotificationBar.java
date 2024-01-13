package main.creatorclasses.subscription;

import java.util.ArrayList;

public final class NotificationBar {
    private String subscriber;
    private ArrayList<String> notifications = new ArrayList<>();

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(final String subscriber) {
        this.subscriber = subscriber;
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(final ArrayList<String> notifications) {
        this.notifications = notifications;
    }
}
