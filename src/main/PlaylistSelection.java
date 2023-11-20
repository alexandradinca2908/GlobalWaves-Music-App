package main;

import fileio.input.SongInput;

public class PlaylistSelection extends ItemSelection {
    private Playlist playlist;

    public PlaylistSelection() {
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
