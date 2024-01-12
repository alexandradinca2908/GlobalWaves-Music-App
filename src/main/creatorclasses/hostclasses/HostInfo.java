package main.creatorclasses.hostclasses;

import fileio.input.UserInput;

import java.util.ArrayList;

public final class HostInfo {
    private UserInput host;
    private ArrayList<Announcement> announcements = new ArrayList<>();

    public UserInput getHost() {
        return host;
    }

    public void setHost(final UserInput host) {
        this.host = host;
    }

    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(final ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }
}
