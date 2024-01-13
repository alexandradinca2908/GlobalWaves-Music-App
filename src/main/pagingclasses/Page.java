package main.pagingclasses;

import fileio.input.UserInput;
import main.creatorclasses.artistclasses.Management;
import main.creatorclasses.hostclasses.HostInfo;
import main.playlistclasses.UserData;

public final class Page {
    private UserInput pageOwner;
    private UserData userData;
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

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(final UserData userPlaylists) {
        this.userData = userPlaylists;
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
