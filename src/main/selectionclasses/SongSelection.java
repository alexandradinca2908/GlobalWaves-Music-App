package main.selectionclasses;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.visitorpattern.visitorobjectnode.VisitableObjectNode;
import main.visitorpattern.visitorobjectnode.VisitorObjectNode;
import main.visitorpattern.visitorstring.VisitableString;
import main.visitorpattern.visitorstring.VisitorString;

public final class SongSelection
        extends ItemSelection
        implements VisitableString, VisitableObjectNode {
    private SongInput song;

    public SongSelection() {
    }

    public SongInput getSong() {
        return song;
    }

    public void setSong(final SongInput song) {
        this.song = song;
    }

    public void updateRemainingTime(final int crtTimestamp) {
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

    @Override
    public String acceptString(final VisitorString visitor) {
        return visitor.visitString(this);
    }

    @Override
    public ObjectNode acceptObjectNode(final VisitorObjectNode visitor) {
        return visitor.visitObjectNode(this);
    }
}
