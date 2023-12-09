package main.SelectionClasses;

import main.PlaylistClasses.Album;

public final class AlbumSelection extends PlaylistSelection {
    private Album album;

    public AlbumSelection() {
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(final Album album) {
        this.album = album;
    }
}
