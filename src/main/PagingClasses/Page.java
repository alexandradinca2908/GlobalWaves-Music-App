package main.PagingClasses;

import fileio.input.UserInput;
import main.CreatorClasses.ArtistClasses.Management;
import main.CreatorClasses.HostClasses.Announcement;
import main.CreatorClasses.HostClasses.HostInfo;
import main.PlaylistClasses.UserPlaylists;

import java.util.ArrayList;

public final class Page {
    private UserInput pageOwner;
    private UserPlaylists userPlaylists;
    private String currentPage = "HomePage";
    //  All artist extras
    private Management management;
    //  Host announcements
    private HostInfo hostInfo;

    public UserInput getPageOwner() {
        return pageOwner;
    }

    public void setPageOwner(final UserInput pageOwner) {
        this.pageOwner = pageOwner;
    }

    public UserPlaylists getUserPlaylists() {
        return userPlaylists;
    }

    public void setUserPlaylists(final UserPlaylists userPlaylists) {
        this.userPlaylists = userPlaylists;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(final String currentPage) {
        this.currentPage = currentPage;
    }

    public Management getManagement() {
        return management;
    }

    public void setManagement(final Management management) {
        this.management = management;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }
}
