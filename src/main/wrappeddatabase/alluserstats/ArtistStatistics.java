package main.wrappeddatabase.alluserstats;

import fileio.input.UserInput;

import java.util.HashMap;

public final class ArtistStatistics extends GeneralStatistics {
    private UserInput artist;
    private HashMap<UserInput, Integer> topFans = new HashMap<>();

    public ArtistStatistics(final UserInput artist) {
        this.artist = artist;
    }

    public HashMap<UserInput, Integer> getTopFans() {
        return topFans;
    }

    public void setTopFans(final HashMap<UserInput, Integer> topListeners) {
        this.topFans = topListeners;
    }

    public UserInput getArtist() {
        return artist;
    }

    public void setArtist(final UserInput artist) {
        this.artist = artist;
    }
}
