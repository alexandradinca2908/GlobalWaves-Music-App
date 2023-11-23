package main;

import fileio.input.SongInput;

public class PlaylistSelection extends ItemSelection {
    private Playlist playlist;
    private int startTimestamp;
    private int stopTimestamp;
    public PlaylistSelection() {
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(int startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public int getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(int stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    @Override
    public void updateRemainingTime(int crtTimestamp) {
        if (!isPaused()) {
            int remainingTime = this.getRemainingTime() - (crtTimestamp - this.getStartTime());

            if (remainingTime < 0) {
                if (this.getRepeat().equals("Repeat All")) {
                    //  Update time
                    while (remainingTime < 0) {
                        remainingTime += this.getPlaylist().getDuration();
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
}
