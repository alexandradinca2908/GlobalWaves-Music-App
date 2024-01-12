package main.WrappedDatabase.AllUserStats;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import main.PlaylistClasses.Album;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneralStatistics {
    private HashMap<String, Integer> topSongs = new HashMap<>();
    private HashMap<String, Integer> topAlbums = new HashMap<>();
    private HashMap<EpisodeInput, Integer> topEpisodes = new HashMap<>();
    private ArrayList<UserInput> listeners = new ArrayList<>();

    public HashMap<String, Integer> getTopSongs() {
        return topSongs;
    }

    public void setTopSongs(final HashMap<String, Integer> topSongs) {
        this.topSongs = topSongs;
    }

    public HashMap<String, Integer> getTopAlbums() {
        return topAlbums;
    }

    public void setTopAlbums(final HashMap<String, Integer> topAlbums) {
        this.topAlbums = topAlbums;
    }

    public HashMap<EpisodeInput, Integer> getTopEpisodes() {
        return topEpisodes;
    }

    public void setTopEpisodes(final HashMap<EpisodeInput, Integer> topEpisodes) {
        this.topEpisodes = topEpisodes;
    }

    public ArrayList<UserInput> getListeners() {
        return listeners;
    }

    public void setListeners(final ArrayList<UserInput> listeners) {
        this.listeners = listeners;
    }
}
