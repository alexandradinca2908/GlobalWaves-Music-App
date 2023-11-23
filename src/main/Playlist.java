package main;

import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;

public class Playlist {
    private String name;
    private ArrayList<SongInput> songs = new ArrayList<>();
    private boolean visibility = true;
    private ArrayList<String> followers = new ArrayList<>();
    private String owner;
    private ArrayList<SongInput> originalSongOrder;

    public Playlist(){
    }

    public Playlist(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<SongInput> getOriginalSongOrder() {
        return originalSongOrder;
    }

    public void setOriginalSongOrder(ArrayList<SongInput> originalSongOrder) {
        this.originalSongOrder = originalSongOrder;
    }

    public int getDuration() {
        int duration = 0;
        for (SongInput song : this.songs) {
            duration += song.getDuration();
        }
        return duration;
    }
}
