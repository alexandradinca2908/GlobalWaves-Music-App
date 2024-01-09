package main.WrappedDatabase;

import main.WrappedDatabase.AllUserStats.ArtistStatistics;
import main.WrappedDatabase.AllUserStats.HostStatistics;
import main.WrappedDatabase.AllUserStats.UserStatistics;

import java.util.ArrayList;

public final class Statistics {
    private ArrayList<UserStatistics> usersStatistics = new ArrayList<>();
    private ArrayList<ArtistStatistics> artistsStatistics = new ArrayList<>();
    private ArrayList<HostStatistics> hostsStatistics = new ArrayList<>();

    private static Statistics wrappedStats;

    private Statistics(){
    }

    public static Statistics getWrappedStats() {
        if (wrappedStats == null) {
            wrappedStats = new Statistics();
        }
        return wrappedStats;
    }

    public ArrayList<UserStatistics> getUsersStatistics() {
        return usersStatistics;
    }

    public void setUsersStatistics(ArrayList<UserStatistics> usersStatistics) {
        this.usersStatistics = usersStatistics;
    }

    public ArrayList<ArtistStatistics> getArtistsStatistics() {
        return artistsStatistics;
    }

    public void setArtistsStatistics(ArrayList<ArtistStatistics> artistsStatistics) {
        this.artistsStatistics = artistsStatistics;
    }

    public ArrayList<HostStatistics> getHostsStatistics() {
        return hostsStatistics;
    }

    public void setHostsStatistics(ArrayList<HostStatistics> hostsStatistics) {
        this.hostsStatistics = hostsStatistics;
    }
}