package main;

import fileio.input.SongInput;

public class SongLikes implements Comparable<SongLikes>{
    private SongInput song;
    private int likes;


    @Override
    public int compareTo(SongLikes songLikes) {
        return this.likes - songLikes.likes;
    }

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
