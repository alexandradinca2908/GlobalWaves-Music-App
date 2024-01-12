package main.pagingclasses;

import fileio.input.UserInput;
import main.creatorclasses.artistclasses.Management;
import main.creatorclasses.hostclasses.HostInfo;
import main.playlistclasses.UserPlaylists;

public final class Page {
    private UserInput pageOwner;
    private UserPlaylists userPlaylists;
    private String currentPage = "Home";
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

    public void setHostInfo(final HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }
}
