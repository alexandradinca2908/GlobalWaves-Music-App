package main.PagingClasses;

import fileio.input.UserInput;
import main.ArtistClasses.Management;
import main.PlaylistClasses.UserPlaylists;

public final class Page {
    private UserInput pageOwner;
    private UserPlaylists userPlaylists;
    private String currentPage = "HomePage";
    private Management management;

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
}
