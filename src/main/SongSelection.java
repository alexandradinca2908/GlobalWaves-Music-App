package main;

import fileio.input.SongInput;

public class SongSelection extends ItemSelection {
    private SongInput song;

    public SongSelection() {
    }

    public SongInput getSong() {
        return song;
    }

    public void setSong(SongInput song) {
        this.song = song;
    }
}
