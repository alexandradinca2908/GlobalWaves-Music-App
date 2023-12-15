package main.PlaylistClasses;

import fileio.input.SongInput;

import java.util.ArrayList;

public class Playlist implements Comparable<Playlist> {
    private String name;
    private ArrayList<SongInput> songs = new ArrayList<>();
    private boolean visibility = true;
    private ArrayList<String> followers = new ArrayList<>();
    private String owner;

    /**
     * Compare two playlists by followers
     * @param playlist the object to be compared.
     * @return
     */
    @Override
    public int compareTo(final Playlist playlist) {
        return this.followers.size() - playlist.getFollowers().size();
    }

    public Playlist() {
    }

    public Playlist(final String name, final String owner) {
        this.name = name;
        this.owner = owner;
    }

    /**
     * Getter for name
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name Playlist name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for songs
     * @return All songs
     */
    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    /**
     * Setter for songs
     * @param songs Playlist Songs
     */
    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    /**
     * Getter for visibility
     * @return Visibility
     */
    public boolean isVisibility() {
        return visibility;
    }

    /**
     * Setter for visibility
     * @param visibility Visibility
     */
    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * Getter for followers
     * @return Followers
     */
    public ArrayList<String> getFollowers() {
        return followers;
    }

    /**
     * Setter for followers
     * @param followers Followers
     */
    public void setFollowers(final ArrayList<String> followers) {
        this.followers = followers;
    }

    /**
     * Getter for owner
     * @return Owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Setter for owner
     * @param owner Owner
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * This method calculates the duration of all songs in the playlist
     *
     * @return The duration of the playlist
     */
    public int getDuration() {
        int duration = 0;
        for (SongInput song : this.songs) {
            duration += song.getDuration();
        }
        return duration;
    }
}
