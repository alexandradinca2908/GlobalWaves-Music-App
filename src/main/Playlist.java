package main;

import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;

public class Playlist {
    private String name;
    private ArrayList<SongInput> songs = new ArrayList<>();
    private boolean visibility = true;
    private ArrayList<UserInput> followers = new ArrayList<>();
    private String owner;

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

    public ArrayList<UserInput> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<UserInput> followers) {
        this.followers = followers;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getDuration() {
        int duration = 0;
        for (SongInput song : this.songs) {
            duration += song.getDuration();
        }
        return duration;
    }
}
