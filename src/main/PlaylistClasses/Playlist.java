package main.PlaylistClasses;

import fileio.input.SongInput;

import java.util.ArrayList;

public class Playlist implements Comparable<Playlist> {
    private String name;
    private ArrayList<SongInput> songs = new ArrayList<>();
    private boolean visibility = true;
    private ArrayList<String> followers = new ArrayList<>();
    private String owner;

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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(final ArrayList<String> followers) {
        this.followers = followers;
    }

    public String getOwner() {
        return owner;
    }

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
