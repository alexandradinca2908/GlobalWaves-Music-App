package main.SelectionClasses.Playlists;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.PlaylistClasses.Album;
import main.SelectionClasses.ItemSelection;
import main.VisitorPattern.VisitorObjectNode.VisitableObjectNode;
import main.VisitorPattern.VisitorObjectNode.VisitorObjectNode;
import main.VisitorPattern.VisitorString.VisitableString;
import main.VisitorPattern.VisitorString.VisitorString;

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
    public void updateRemainingTime(final int crtTimestamp) {
        if (!isPaused()) {
            int remainingTime = this.getRemainingTime() - (crtTimestamp - this.getStartTime());

            if (remainingTime < 0) {
                if (this.getRepeat().equals("Repeat All")) {
                    //  Update time
                    while (remainingTime < 0) {
                        remainingTime += this.getAlbum().getDuration();
                    }
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                } else if (this.getRepeat().equals("Repeat Current Song")) {
                    //  Go back to initial timestamp
                    while (remainingTime <= this.stopTimestamp) {
                        remainingTime += (this.startTimestamp - this.stopTimestamp);
                    }
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                } else {
                    //  Stop playlist
                    this.setRemainingTime(0);
                    this.setPaused(true);
                }

            } else {
                if (this.getRepeat().equals("Repeat Current Song")) {
                    //  Go back to initial timestamp
                    while (remainingTime <= this.stopTimestamp) {
                        remainingTime += (this.startTimestamp - this.stopTimestamp);
                    }
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                } else {
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);
                }
            }
        }
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

    @Override
    public String acceptString(final VisitorString visitor) {
        return visitor.visitString(this);
    }

    @Override
    public ObjectNode acceptObjectNode(final VisitorObjectNode visitor) {
        return visitor.visitObjectNode(this);
    }
}
