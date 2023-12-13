package main.SelectionClasses;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.PodcastInput;
import main.VisitorPattern.VisitorObjectNode.VisitorObjectNode;
import main.VisitorPattern.VisitorString.VisitableString;
import main.VisitorPattern.VisitorString.VisitorString;

public final class PodcastSelection extends ItemSelection implements VisitableString {
    private PodcastInput podcast;

    public PodcastSelection() {
    }

    public PodcastInput getPodcast() {
        return podcast;
    }

    public void setPodcast(final PodcastInput podcast) {
        this.podcast = podcast;
    }

    @Override
    public void updateRemainingTime(final int crtTimestamp) {
        if (!isPaused()) {
            int remainingTime = this.getRemainingTime() - (crtTimestamp - getStartTime());

            if (remainingTime < 0) {
                if (this.getRepeat().equals("Repeat Once")) {
                    //  Update repeat
                    this.setRepeat("No Repeat");

                    //  Update times
                    this.setRemainingTime(this.getPodcast().getDuration() + remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  Check if time is still negative
                    if (this.getRemainingTime() < 0) {
                        this.setRemainingTime(0);
                        this.setPaused(true);
                    }

                } else if (this.getRepeat().equals("Repeat Infinite")) {
                    //  Update times
                    while (remainingTime < 0) {
                        remainingTime += this.getPodcast().getDuration();
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
