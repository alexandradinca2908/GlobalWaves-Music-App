package main.playlistclasses;

import main.likeclasses.SongLikes;

import java.util.ArrayList;

public final class Album extends Playlist {
    private int releaseYear;
    private String description;
    private ArrayList<SongLikes> songsWithLikes = new ArrayList<>();
    //  Name, songs, owner are inherited

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<SongLikes> getSongsWithLikes() {
        return songsWithLikes;
    }

    public void setSongsWithLikes(final ArrayList<SongLikes> songsWithLikes) {
        this.songsWithLikes = songsWithLikes;
    }

    /**
     * Calculates the number of likes from all songs in the album
     * @return Total number of likes
     */
    public int calculateAlbumLikes() {
        int allLikes = 0;
        for (SongLikes song : songsWithLikes) {
            allLikes += song.getLikes();
        }

        return allLikes;
    }
}
