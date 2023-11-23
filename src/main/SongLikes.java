package main;

import fileio.input.SongInput;

public class SongLikes {
    private SongInput song;
    private int likes;

    public SongLikes() {
    }

    public SongInput getSong() {
        return song;
    }

    public void setSong(SongInput song) {
        this.song = song;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
