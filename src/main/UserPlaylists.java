package main;

import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;

public class UserPlaylists {
    private UserInput user;
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<SongInput> likedSongs = new ArrayList<>();
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();

    public UserPlaylists(){
    }

    public UserInput getUser() {
        return user;
    }

    public void setUser(UserInput user) {
        this.user = user;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> userPlaylists) {
        this.playlists = userPlaylists;
    }

    public ArrayList<SongInput> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(ArrayList<SongInput> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public ArrayList<Playlist> getFollowedPlaylists() {
        return followedPlaylists;
    }

    public void setFollowedPlaylists(ArrayList<Playlist> followedPlaylists) {
        this.followedPlaylists = followedPlaylists;
    }
}
