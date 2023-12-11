package main.CreatorClasses.HostClasses;

import fileio.input.UserInput;

import java.util.ArrayList;

public final class HostInfo {
    private UserInput host;
    private ArrayList<Announcement> announcements = new ArrayList<>();

    public UserInput getHost() {
        return host;
    }

    public void setHost(UserInput host) {
        this.host = host;
    }

    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }
}
