package main.PlaylistClasses;

public final class Album extends Playlist {
    private int releaseYear;
    private String description;
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
}
