package main.SelectionClasses.Playlists;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.PlaylistClasses.Album;
import main.SelectionClasses.ItemSelection;
import main.VisitorPattern.VisitorObjectNode.VisitableObjectNode;
import main.VisitorPattern.VisitorObjectNode.VisitorObjectNode;
import main.VisitorPattern.VisitorString.VisitableString;
import main.VisitorPattern.VisitorString.VisitorString;

public final class AlbumSelection extends ItemSelection implements VisitableString, VisitableObjectNode {
    private Album album;
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
     * @param playlist The specified playlist
     */
    public static void setIntervals(final PlaylistSelection playlist) {
        int remainingTime = playlist.getRemainingTime();
        int duration = playlist.getPlaylist().getDuration();

        //  Now we find the song that needs repetition
        for (SongInput song : playlist.getPlaylist().getSongs()) {
            duration -= song.getDuration();

            if (duration < remainingTime) {
                playlist.setStartTimestamp(duration + song.getDuration());
                playlist.setStopTimestamp(duration);

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
