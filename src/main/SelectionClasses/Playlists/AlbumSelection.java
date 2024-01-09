package main.SelectionClasses.Playlists;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.PlaylistClasses.Album;
import main.SelectionClasses.ItemSelection;
import main.VisitorPattern.VisitorObjectNode.VisitableObjectNode;
import main.VisitorPattern.VisitorObjectNode.VisitorObjectNode;
import main.VisitorPattern.VisitorString.VisitableString;
import main.VisitorPattern.VisitorString.VisitorString;
import main.WrappedDatabase.AllUserStats.ArtistStatistics;
import main.WrappedDatabase.Statistics;

import java.lang.reflect.Array;
import java.util.ArrayList;

public final class AlbumSelection
        extends ItemSelection
        implements VisitableString, VisitableObjectNode {
    private Album album;
    private ArrayList<SongInput> shuffledAlbum = new ArrayList<>();
    private int startTimestamp;
    private int stopTimestamp;

    public AlbumSelection() {
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(final Album album) {
        this.album = album;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(final int startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public int getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(final int stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public ArrayList<SongInput> getShuffledAlbum() {
        return shuffledAlbum;
    }

    public void setShuffledAlbum(final ArrayList<SongInput> shuffledAlbum) {
        this.shuffledAlbum = shuffledAlbum;
    }

    @Override
    public void updateRemainingTime(final int crtTimestamp,
                                    final ArrayList<Album> albums) {
        if (!isPaused()) {
            int remainingTime = this.getRemainingTime() - (crtTimestamp - this.getStartTime());

            //  For artist album stats
            ArtistStatistics crtArtist = null;
            for (ArtistStatistics artistStatistics
                    : Statistics.getWrappedStats().getArtistsStatistics()) {
                if (artistStatistics.getArtist().getUsername()
                        .equals(this.getAlbum().getOwner())) {
                    crtArtist = artistStatistics;
                    break;
                }
            }

            if (remainingTime < 0) {
                if (this.getRepeat().equals("Repeat All")) {
                    //  Count replays
                    int replays = 0;

                    //  Update time
                    while (remainingTime < 0) {
                        remainingTime += this.getAlbum().getDuration();
                        replays++;

                        //  Increase album the listen count
                        int count = crtArtist.getTopAlbums()
                                .get(this.getAlbum());
                        crtArtist.getTopAlbums().put(this.getAlbum(), count + 1);
                    }
                    int prevRemainingTime = this.getRemainingTime();

                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  Update Wrapped
                    //  First add the songs before any replay
                    int duration = this.getAlbum().getDuration();

                    ArrayList<SongInput> crtAlbum = null;
                    if (!this.isShuffle()) {
                        crtAlbum = this.getAlbum().getSongs();
                    } else {
                        crtAlbum = this.getShuffledAlbum();
                    }

                    for (SongInput song : crtAlbum) {
                        duration -= song.getDuration();

                        if (duration < prevRemainingTime
                                && crtAlbum.indexOf(song) != 0) {
                            this.updateWrappedForSong(song, albums);
                        }
                    }

                    //  Now add all the songs if there is more than one replay
                    while (replays > 1) {
                        for (SongInput song : crtAlbum) {
                            this.updateWrappedForSong(song, albums);
                        }
                        replays--;
                    }

                    //  Lastly add the songs from crt replay
                    duration = this.getAlbum().getDuration();

                    for (SongInput song : crtAlbum) {
                        duration -= song.getDuration();

                        if (duration >= remainingTime) {
                            this.updateWrappedForSong(song, albums);
                        } else {
                            this.updateWrappedForSong(song, albums);
                            break;
                        }
                    }

                } else if (this.getRepeat().equals("Repeat Current Song")) {
                    //  Keep track of replays
                    int replays = 0;

                    //  Go back to initial timestamp
                    while (remainingTime <= this.stopTimestamp) {
                        remainingTime += (this.startTimestamp - this.stopTimestamp);
                        replays++;
                    }
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  Update Wrapped
                    //  Find crt song
                    int duration = this.getAlbum().getDuration();
                    SongInput crtSong = null;

                    ArrayList<SongInput> crtAlbum = null;
                    if (!this.isShuffle()) {
                        crtAlbum = this.getAlbum().getSongs();
                    } else {
                        crtAlbum = this.getShuffledAlbum();
                    }

                    for (SongInput song : crtAlbum) {
                        duration -= song.getDuration();

                        if (duration < remainingTime) {
                            crtSong = song;
                            break;
                        }
                    }

                    while (replays > 0) {
                        this.updateWrappedForSong(crtSong, albums);
                        replays--;
                    }
                } else {
                    //  Update Wrapped
                    int duration = this.getAlbum().getDuration();
                    int prevRemainingTime = this.getRemainingTime();

                    ArrayList<SongInput> crtAlbum = null;
                    if (!this.isShuffle()) {
                        crtAlbum = this.getAlbum().getSongs();
                    } else {
                        crtAlbum = this.getShuffledAlbum();
                    }

                    for (SongInput song : crtAlbum) {
                        duration -= song.getDuration();

                        if (duration < prevRemainingTime
                                && crtAlbum.indexOf(song) != 0) {
                            this.updateWrappedForSong(song, albums);
                        }
                    }

                    //  Stop playlist
                    this.setRemainingTime(0);
                    this.setPaused(true);
                }

            } else {
                if (this.getRepeat().equals("Repeat Current Song")) {
                    //  Keep track of replays
                    int replays = 0;

                    //  Go back to initial timestamp
                    while (remainingTime <= this.stopTimestamp) {
                        remainingTime += (this.startTimestamp - this.stopTimestamp);
                        replays++;
                    }
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  Update Wrapped
                    //  Find crt song
                    int duration = this.getAlbum().getDuration();
                    SongInput crtSong = null;

                    ArrayList<SongInput> crtAlbum = null;
                    if (!this.isShuffle()) {
                        crtAlbum = this.getAlbum().getSongs();
                    } else {
                        crtAlbum = this.getShuffledAlbum();
                    }

                    for (SongInput song : crtAlbum) {
                        duration -= song.getDuration();

                        if (duration < remainingTime) {
                            crtSong = song;
                            break;
                        }
                    }

                    while (replays > 0) {
                        this.updateWrappedForSong(crtSong, albums);
                        replays--;
                    }

                } else {
                    //  Update Wrapped
                    int prevRemainingTime = this.getRemainingTime();

                    //  Update timestamps
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  If the song hasn't finished since last update
                    //  We won't add it to the database
                    int duration = this.getAlbum().getDuration();
                    boolean skipWrapped = false;

                    ArrayList<SongInput> crtAlbum = null;
                    if (!this.isShuffle()) {
                        crtAlbum = this.getAlbum().getSongs();
                    } else {
                        crtAlbum = this.getShuffledAlbum();
                    }

                    //  Check song
                    for (SongInput song : crtAlbum) {
                        if (duration > this.getRemainingTime()
                                && duration < prevRemainingTime) {
                            break;
                        }
                        if (duration > this.getRemainingTime()
                                && duration > prevRemainingTime) {
                            skipWrapped = true;
                            break;
                        }

                        duration -= song.getDuration();
                    }

                    if (!skipWrapped) {
                        duration = this.getAlbum().getDuration();
                        for (SongInput song : crtAlbum) {
                            if (duration <= prevRemainingTime) {
                                if (duration < this.getRemainingTime()) {
                                    break;
                                } else {
                                    if (crtAlbum.indexOf(song) != 0) {
                                        this.updateWrappedForSong(song, albums);
                                    }
                                }
                            }
                            duration -= song.getDuration();
                        }
                    }
                }
            }
        }
    }

    @Override
    public String acceptString(final VisitorString visitor) {
        return visitor.visitString(this);
    }

    @Override
    public ObjectNode acceptObjectNode(final VisitorObjectNode visitor) {
        return visitor.visitObjectNode(this);
    }

    /**
     * This method sets the interval of the current song within a playlist
     *
     * @param album The specified album
     */
    public static void setIntervals(final AlbumSelection album) {
        int remainingTime = album.getRemainingTime();
        int duration = album.getAlbum().getDuration();

        //  Now we find the song that needs repetition
        for (SongInput song : album.getAlbum().getSongs()) {
            duration -= song.getDuration();

            if (duration < remainingTime) {
                album.setStartTimestamp(duration + song.getDuration());
                album.setStopTimestamp(duration);

                break;
            }
        }
    }

    /**
     * This method sets the interval of the current song within a playlist
     *
     * @param album The specified album
     */
    public static void setIntervalsShuffle(final AlbumSelection album) {
        int remainingTime = album.getRemainingTime();
        int duration = album.getAlbum().getDuration();

        //  Now we find the song that needs repetition
        for (SongInput song : album.getShuffledAlbum()) {
            duration -= song.getDuration();

            if (duration < remainingTime) {
                album.setStartTimestamp(duration + song.getDuration());
                album.setStopTimestamp(duration);

                break;
            }
        }
    }
}
