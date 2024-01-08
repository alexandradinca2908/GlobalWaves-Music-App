package main.WrappedDatabase.AllUserStats;

import fileio.input.UserInput;

import java.util.HashMap;

public final class ArtistStatistics extends GeneralStatistics {
    private UserInput artist;
    private HashMap<UserInput, Integer> topFans = new HashMap<>();

    public HashMap<UserInput, Integer> getTopFans() {
        return topFans;
    }

    public void setTopFans(final HashMap<UserInput, Integer> topListeners) {
        this.topFans = topListeners;
    }

    public UserInput getArtist() {
        return artist;
    }

    public void setArtist(UserInput artist) {
        this.artist = artist;
    }
}
