package main;

import fileio.input.SongInput;

public class ItemSelection {
    private String user;
    private int startTime;
    private int remainingTime;
    private String repeat = "No Repeat";
    private boolean shuffle = false;
    private boolean paused = false;

    public ItemSelection() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void updateRemainingTime(int crtTimestamp) {
        if (!isPaused()) {
            int remainingTime = this.remainingTime - (crtTimestamp - startTime);

            if (remainingTime < 0) {
                remainingTime = 0;
                this.paused = true;
            }

            this.remainingTime = remainingTime;
            this.startTime = crtTimestamp;
        }
    }
}
