package main.wrappeddatabase.alluserstats;

import fileio.input.EpisodeInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneralStatistics {
    private HashMap<String, Integer> topSongs = new HashMap<>();
    private HashMap<String, Integer> topAlbums = new HashMap<>();
    private HashMap<EpisodeInput, Integer> topEpisodes = new HashMap<>();
    private ArrayList<UserInput> listeners = new ArrayList<>();

    /**
     * Getter for topSongs
     * @return topSongs
     */
    public HashMap<String, Integer> getTopSongs() {
        return topSongs;
    }

    /**
     * Setter for topSongs
     * @param topSongs topSongs
     */
    public void setTopSongs(final HashMap<String, Integer> topSongs) {
        this.topSongs = topSongs;
    }

    /**
     * Getter for topAlbums
     * @return topAlbums
     */
    public HashMap<String, Integer> getTopAlbums() {
        return topAlbums;
    }

    /**
     * Setter for topAlbums
     * @param topAlbums topAlbums
     */
    public void setTopAlbums(final HashMap<String, Integer> topAlbums) {
        this.topAlbums = topAlbums;
    }

    /**
     * Getter for topEpisodes
     * @return topEpisodes
     */
    public HashMap<EpisodeInput, Integer> getTopEpisodes() {
        return topEpisodes;
    }

    /**
     * Setter for topEpisodes
     * @param topEpisodes topEpisodes
     */
    public void setTopEpisodes(final HashMap<EpisodeInput, Integer> topEpisodes) {
        this.topEpisodes = topEpisodes;
    }

    /**
     * Getter for listeners
     * @return listeners
     */
    public ArrayList<UserInput> getListeners() {
        return listeners;
    }

    /**
     * Setter for listeners
     * @param listeners listeners
     */
    public void setListeners(final ArrayList<UserInput> listeners) {
        this.listeners = listeners;
    }
}
