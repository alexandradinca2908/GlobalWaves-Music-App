package main.creatorclasses.subscription;

import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.Observable;

public final class CreatorChannel extends Observable {
    private UserInput creator;
    private ArrayList<String> subscribers = new ArrayList<>();

    public UserInput getCreator() {
        return creator;
    }

    public void setCreator(final UserInput creator) {
        this.creator = creator;
    }

    public ArrayList<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(final ArrayList<String> subscribers) {
        this.subscribers = subscribers;
    }
}
