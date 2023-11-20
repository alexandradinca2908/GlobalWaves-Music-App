package main;

import fileio.input.PodcastInput;

public class PodcastSelection extends ItemSelection {
    private PodcastInput podcast;

    public PodcastSelection() {
    }

    public PodcastInput getPodcast() {
        return podcast;
    }

    public void setPodcast(PodcastInput podcast) {
        this.podcast = podcast;
    }
}
