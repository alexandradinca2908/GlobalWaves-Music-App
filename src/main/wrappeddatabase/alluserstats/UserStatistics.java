package main.wrappeddatabase.alluserstats;

import fileio.input.UserInput;
import java.util.HashMap;

public final class UserStatistics extends GeneralStatistics {
    private UserInput user;
    private HashMap<String, Integer> topArtists = new HashMap<>();
    private HashMap<String, Integer> topGenres = new HashMap<>();

    public UserStatistics(final UserInput user) {
        this.user = user;
    }

    public UserInput getUser() {
        return user;
    }

    public void setUser(final UserInput user) {
        this.user = user;
    }

    public HashMap<String, Integer> getTopArtists() {
        return topArtists;
    }

    public void setTopArtists(final HashMap<String, Integer> topArtists) {
        this.topArtists = topArtists;
    }

    public HashMap<String, Integer> getTopGenres() {
        return topGenres;
    }

    public void setTopGenres(final HashMap<String, Integer> topGenres) {
        this.topGenres = topGenres;
    }
}
