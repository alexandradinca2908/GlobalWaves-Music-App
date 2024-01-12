package main.monetization;

import fileio.input.SongInput;

import java.util.ArrayList;

public final class PremiumUser {
    private String user;
    private ArrayList<SongInput> playedSongs = new ArrayList<>();

    public PremiumUser() {
    }

    public PremiumUser(final String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public ArrayList<SongInput> getPlayedSongs() {
        return playedSongs;
    }

    public void setPlayedSongs(final ArrayList<SongInput> playedSongs) {
        this.playedSongs = playedSongs;
    }
}
