package main.creatorclasses.artistclasses;

import fileio.input.UserInput;

import java.util.ArrayList;

public final class Management {
    private UserInput artist;
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Merch> merches = new ArrayList<>();

    public Management() {
    }

    public UserInput getArtist() {
        return artist;
    }

    public void setArtist(final UserInput artist) {
        this.artist = artist;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(final ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Merch> getMerches() {
        return merches;
    }

    public void setMerches(final ArrayList<Merch> merches) {
        this.merches = merches;
    }
}
