package main.selectionclasses;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import main.monetization.PremiumUser;
import main.playlistclasses.Album;
import main.visitorpattern.visitorobjectnode.VisitorObjectNode;
import main.visitorpattern.visitorstring.VisitableString;
import main.visitorpattern.visitorstring.VisitorString;
import main.wrappeddatabase.Statistics;
import main.wrappeddatabase.alluserstats.HostStatistics;
import main.wrappeddatabase.alluserstats.UserStatistics;

import java.util.ArrayList;

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
    public void updateRemainingTime(final int crtTimestamp,
                                    final ArrayList<Album> albums,
                                    final ArrayList<PremiumUser> premiumUsers) {
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
                //  Update Wrapped
                int prevRemainingTime = this.getRemainingTime();

                //  Update timestamps
                this.setRemainingTime(remainingTime);
                this.setStartTime(crtTimestamp);

                //  If the song hasn't finished since last update
                //  We won't add it to the database
                int duration = this.getPodcast().getDuration();
                boolean skipWrapped = false;

                ArrayList<EpisodeInput> crtPodcast = this.getPodcast().getEpisodes();

                //  Check song
                for (EpisodeInput episode : crtPodcast) {
                    if (duration >= this.getRemainingTime()
                            && duration < prevRemainingTime) {
                        break;
                    }
                    if (duration <= this.getRemainingTime()
                            && duration <= prevRemainingTime) {
                        skipWrapped = true;
                        break;
                    }

                    duration -= episode.getDuration();
                }

                if (!skipWrapped) {
                    duration = this.getPodcast().getDuration();
                    for (EpisodeInput episode : crtPodcast) {
                        if (duration < prevRemainingTime) {
                            if (duration < this.getRemainingTime()) {
                                break;
                            } else {
                                if (crtPodcast.indexOf(episode) != 0) {
                                    this.updateWrappedForEpisode(episode);
                                }
                            }
                        }
                        duration -= episode.getDuration();
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
     * Adds selected episode to wrapped
     * @param selectedEpisode Selected episode
     */
    public void updateWrappedForEpisode(final EpisodeInput selectedEpisode) {
        //  User stats
        UserStatistics crtUser = null;
        for (UserStatistics userStatistics
                : Statistics.getWrappedStats().getUsersStatistics()) {
            if (userStatistics.getUser().getUsername()
                    .equals(this.getUser())) {
                crtUser = userStatistics;
                break;
            }
        }

        //  First episode
        if (crtUser.getTopEpisodes().containsKey(selectedEpisode)) {
            //  Increase the listen count if the episode exists
            int count = crtUser.getTopEpisodes().get(selectedEpisode);
            crtUser.getTopEpisodes().put(selectedEpisode, count + 1);
        } else {
            //  Add the episode
            crtUser.getTopEpisodes().put(selectedEpisode, 1);
        }

        //  Artist stats
        HostStatistics crtHost = null;
        for (HostStatistics hostStatistics
                : Statistics.getWrappedStats().getHostsStatistics()) {
            if (hostStatistics.getHost().getUsername()
                    .equals(this.getPodcast().getOwner())) {
                crtHost = hostStatistics;
                break;
            }
        }

        //  Episode
        if (crtHost != null) {
            if (crtHost.getTopEpisodes().containsKey(selectedEpisode)) {
                //  Increase the listen count if the song exists
                int count = crtHost.getTopEpisodes().get(selectedEpisode);
                crtHost.getTopEpisodes().put(selectedEpisode, count + 1);
            } else {
                //  Add the song if it's the first time being listened to
                crtHost.getTopEpisodes().put(selectedEpisode, 1);
            }
            //  Listeners
            if (!crtHost.getListeners().contains(crtUser.getUser())) {
                crtHost.getListeners().add(crtUser.getUser());
            }
        }
    }
}
