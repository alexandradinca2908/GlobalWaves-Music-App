package main.SelectionClasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.VisitorPattern.Visitable;
import main.VisitorPattern.Visitor;

public class ItemSelection implements Visitable {
    private String user;
    private int startTime;
    private int remainingTime;
    private String repeat = "No Repeat";
    private boolean shuffle = false;
    private boolean paused = false;

    public ItemSelection() {
    }

    /**
     * Getter for user field
     * @return Username
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter for user field
     * @param user Username
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * Getter for start time
     * @return Start time
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Setter for start time
     * @param startTime Start time
     */
    public void setStartTime(final int startTime) {
        this.startTime = startTime;
    }

    /**
     * Getter for remaining time
     * @return Remaining time
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * Setter for remaining time
     * @param remainingTime Remaining time
     */
    public void setRemainingTime(final int remainingTime) {
        this.remainingTime = remainingTime;
    }

    /**
     * Getter for repeat state
     * @return Repeat state
     */
    public String getRepeat() {
        return repeat;
    }

    /**
     * Setter for repeat statte
     * @param repeat Repeat state
     */
    public void setRepeat(final String repeat) {
        this.repeat = repeat;
    }

    /**
     * Getter for shuffle state
     * @return Shuffle state
     */
    public boolean isShuffle() {
        return shuffle;
    }

    /**
     * Setter for shuffle state
     * @param shuffle shuffle state
     */
    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }

    /**
     * Getter for pause state
     * @return Pause state
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Setter for pause state
     * @param paused pause state
     */
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }

    /**
     * This method retrieves the status of the user's player
     *
     * @param reqItem The required item from the user player
     * @param objectMapper The object mapper used for building the JSON object
     * @return The created Status JSON object
     */
    public static ObjectNode getStats(final ItemSelection reqItem,
                                      final ObjectMapper objectMapper) {
        ObjectNode stats = objectMapper.createObjectNode();

        if (reqItem == null) {
            //  If the user does not have an active player, we set default stats
            stats.put("name", "");
            stats.put("remainedTime", 0);
            stats.put("repeat", "No Repeat");
            stats.put("shuffle", false);
            stats.put("paused", true);
        } else {
            //  Downsize item for JSON details
            if (reqItem instanceof SongSelection) {
                SongInput songItem = ((SongSelection) reqItem).getSong();

                //  Check remaining time
                int remainingTime = reqItem.getRemainingTime();

                //  Set name
                if (remainingTime == 0) {
                    stats.put("name", "");
                } else {
                    stats.put("name", songItem.getName());
                }

                //  Set remaining time
                stats.put("remainedTime", remainingTime);

                //  Set repeat status
                stats.put("repeat", reqItem.getRepeat());

                //  Set shuffle
                stats.put("shuffle", reqItem.isShuffle());

                //  Set paused
                stats.put("paused", reqItem.isPaused());

                return stats;

            } else if (reqItem instanceof PlaylistSelection) {
                //  Check remaining time
                int remainingTime = reqItem.getRemainingTime();

                if (remainingTime == 0) {
                    //  Set name
                    stats.put("name", "");

                    //  Set remaining time
                    stats.put("remainedTime", 0);
                } else {
                    //  We find the current song
                    SongInput crtSong = null;

                    int duration = ((PlaylistSelection) reqItem).getPlaylist().getDuration();

                    for (SongInput song : ((PlaylistSelection) reqItem).getPlaylist().getSongs()) {
                        duration -= song.getDuration();

                        if (duration < remainingTime) {
                            crtSong = song;
                            break;
                        }
                    }

                    //  Set name
                    stats.put("name", crtSong.getName());

                    //  Set remaining time
                    stats.put("remainedTime", remainingTime - duration);
                }

                //  Set repeat status
                stats.put("repeat", reqItem.getRepeat());

                //  Set shuffle
                stats.put("shuffle", reqItem.isShuffle());

                //  Set paused
                stats.put("paused", reqItem.isPaused());

                return stats;

            } else if (reqItem instanceof PodcastSelection) {
                //  Check remaining time
                int remainingTime = reqItem.getRemainingTime();

                if (remainingTime == 0) {
                    //  Set name
                    stats.put("name", "");

                    //  Set remaining time
                    stats.put("remainedTime", 0);
                } else {
                    //  We find the current episode
                    EpisodeInput crtEpisode = null;
                    PodcastSelection copyItem = (PodcastSelection) reqItem;
                    int duration = ((PodcastSelection) reqItem).getPodcast().getDuration();

                    for (EpisodeInput episode : copyItem.getPodcast().getEpisodes()) {
                        duration -= episode.getDuration();

                        if (duration < remainingTime) {
                            crtEpisode = episode;
                            break;
                        }
                    }

                    //  Set name
                    stats.put("name", crtEpisode.getName());

                    //  Set remaining time
                    stats.put("remainedTime", remainingTime - duration);
                }

                //  Set repeat status
                stats.put("repeat", reqItem.getRepeat());

                //  Set shuffle
                stats.put("shuffle", reqItem.isShuffle());

                //  Set paused
                stats.put("paused", reqItem.isPaused());

                return stats;
            }
        }

        return stats;
    }

    /**
     * This method updates the remaining time for a playing track
     *
     * @param crtTimestamp Current time
     */
    public void updateRemainingTime(final int crtTimestamp) { };

    /**
     * This method accepts visitors for the ItemSelection subclasses
     *
     * @param visitor Visitor
     * @return Command message
     */
    @Override
    public String accept(final Visitor visitor) {
        return null;
    }
}
