package main.SelectionClasses;

import fileio.input.UserInput;

public final class ArtistSelection extends ItemSelection {
    private UserInput artist;

    public ArtistSelection() {
    }

    public UserInput getArtist() {
        return artist;
    }

    public void setArtist(final UserInput artist) {
        this.artist = artist;
    }
}
