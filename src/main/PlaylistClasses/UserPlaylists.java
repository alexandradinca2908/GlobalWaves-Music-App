package main.PlaylistClasses;

import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;

public final class UserPlaylists {
    private UserInput user;
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<SongInput> likedSongs = new ArrayList<>();
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();
    private ArrayList<Album> albums = new ArrayList<>();
    private ArrayList<PodcastInput> podcasts = new ArrayList<>();

    public UserPlaylists() {
    }

    public UserInput getUser() {
        return user;
    }

    public void setUser(final UserInput user) {
        this.user = user;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(final ArrayList<Playlist> userPlaylists) {
        this.playlists = userPlaylists;
    }

    public ArrayList<SongInput> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(final ArrayList<SongInput> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public ArrayList<Playlist> getFollowedPlaylists() {
        return followedPlaylists;
    }

    public void setFollowedPlaylists(final ArrayList<Playlist> followedPlaylists) {
        this.followedPlaylists = followedPlaylists;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(final ArrayList<Album> albums) {
        this.albums = albums;
    }

    public ArrayList<PodcastInput> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(ArrayList<PodcastInput> podcasts) {
        this.podcasts = podcasts;
    }
}
