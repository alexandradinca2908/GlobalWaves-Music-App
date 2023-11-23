package main;

import fileio.input.SongInput;

public final class SongSelection extends ItemSelection {
    private SongInput song;

    public SongSelection() {
    }

    public SongInput getSong() {
        return song;
    }

    public void setSong(SongInput song) {
        this.song = song;
    }

    @Override
    public void updateRemainingTime(int crtTimestamp) {
        if (!isPaused()) {
            int remainingTime = this.getRemainingTime() - (crtTimestamp - getStartTime());

            if (remainingTime < 0) {
                if (this.getRepeat().equals("Repeat Once")) {
                    //  Update repeat
                    this.setRepeat("No Repeat");

                    //  Update times
                    this.setRemainingTime(this.getSong().getDuration() + remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  Check if time is still negative
                    if (this.getRemainingTime() < 0) {
                        this.setRemainingTime(0);
                        this.setPaused(true);
                    }

                } else if (this.getRepeat().equals("Repeat Infinite")) {
                    //  Update times
                    while (remainingTime < 0) {
                        remainingTime += this.getSong().getDuration();
                    }
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                } else {
                    //  Stop song
                    this.setRemainingTime(0);
                    this.setPaused(true);
                }

            } else {
                this.setRemainingTime(remainingTime);
                this.setStartTime(crtTimestamp);
            }
        }
    }
}
