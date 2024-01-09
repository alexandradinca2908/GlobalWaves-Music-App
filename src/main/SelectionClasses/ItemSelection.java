package main.SelectionClasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.PlaylistClasses.Album;
import main.VisitorPattern.VisitorObjectNode.ObjectNodeClasses.VisitGetStats;
import main.VisitorPattern.VisitorObjectNode.VisitableObjectNode;
import main.VisitorPattern.VisitorObjectNode.VisitorObjectNode;
import main.VisitorPattern.VisitorString.VisitableString;
import main.VisitorPattern.VisitorString.VisitorString;
import main.WrappedDatabase.AllUserStats.ArtistStatistics;
import main.WrappedDatabase.AllUserStats.UserStatistics;
import main.WrappedDatabase.Statistics;

import java.util.ArrayList;

public class ItemSelection implements VisitableString, VisitableObjectNode {
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
            VisitorObjectNode visitGetStats = new VisitGetStats(stats);

            return reqItem.acceptObjectNode(visitGetStats);
        }

        return stats;
    }

    /**
     * This method updates the remaining time for a playing track
     *
     * @param crtTimestamp Current time
     */
    public void updateRemainingTime(final int crtTimestamp,
                                    final ArrayList<Album> albums) { };

    /**
     * This method accepts visitors for the ItemSelection subclasses
     *
     * @param visitor Visitor
     * @return Command message
     */
    @Override
    public String acceptString(final VisitorString visitor) {
        return null;
    }

    /**
     * This method accepts visitors for the ItemSelection subclasses
     *
     * @param visitor Visitor
     * @return Command Output
     */
    @Override
    public ObjectNode acceptObjectNode(final VisitorObjectNode visitor) {
        return null;
    }

    public void updateWrappedForSong(final SongInput selectedSong,
                                     final ArrayList<Album> albums) {
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

        //  First song
        if (crtUser.getTopSongs().containsKey(selectedSong)) {
            //  Increase the listen count if the song exists
            int count = crtUser.getTopSongs().get(selectedSong);
            crtUser.getTopSongs().put(selectedSong, count + 1);
        } else {
            //  Add the song
            crtUser.getTopSongs().put(selectedSong, 1);
        }
        //  Genre
        if (crtUser.getTopGenres().containsKey(selectedSong.getGenre())) {
            //  Increase the listen count if the genre exists
            int count = crtUser.getTopGenres()
                    .get(selectedSong.getGenre());
            crtUser.getTopGenres()
                    .put(selectedSong.getGenre(), count + 1);
        } else {
            //  Add the genre
            crtUser.getTopGenres().put(selectedSong.getGenre(), 1);
        }
        //  Artist
        if (crtUser.getTopArtists().containsKey(selectedSong.getArtist())) {
            //  Increase the listen count if the artist exists
            int count = crtUser.getTopArtists()
                    .get(selectedSong.getArtist());
            crtUser.getTopArtists()
                    .put(selectedSong.getArtist(), count + 1);
        } else {
            //  Add the artist
            crtUser.getTopArtists().put(selectedSong.getArtist(), 1);
        }
        //  Album
        //  Find the album
        Album crtAlbum = null;
        for (Album album : albums) {
            if (album.getName().equals(selectedSong.getAlbum())
                    && album.getOwner().equals(selectedSong.getArtist())) {
                crtAlbum = album;
                break;
            }
        }
        if (crtUser.getTopAlbums().containsKey(crtAlbum)) {
            //  Increase the listen count if the album exists
            int count = crtUser.getTopAlbums()
                    .get(crtAlbum);
            crtUser.getTopAlbums()
                    .put(crtAlbum, count + 1);
        } else {
            //  Add the album
            crtUser.getTopAlbums().put(crtAlbum, 1);
        }

        //  Artist stats
        ArtistStatistics crtArtist = null;
        for (ArtistStatistics artistStatistics
                : Statistics.getWrappedStats().getArtistsStatistics()) {
            if (artistStatistics.getArtist().getUsername()
                    .equals(selectedSong.getArtist())) {
                crtArtist = artistStatistics;
                break;
            }
        }

        //  Song
        if (crtArtist.getTopSongs().containsKey(selectedSong)) {
            //  Increase the listen count if the song exists
            int count = crtArtist.getTopSongs().get(selectedSong);
            crtArtist.getTopSongs().put(selectedSong, count + 1);
        } else {
            //  Add the song if it's the first time being listened to
            crtArtist.getTopSongs().put(selectedSong, 1);
        }
        // Album
        if (crtArtist.getTopAlbums().containsKey(crtAlbum)) {
            //  Increase the listen count if the album exists
            int count = crtArtist.getTopAlbums()
                    .get(crtAlbum);
            crtArtist.getTopAlbums()
                    .put(crtAlbum, count + 1);
        } else {
            //  Add the album
            crtArtist.getTopAlbums().put(crtAlbum, 1);
        }
        //  Fans
        if (crtArtist.getTopFans().containsKey(crtUser.getUser())) {
            //  Increase the listen count if the fan exists
            int count = crtArtist.getTopFans().get(crtUser.getUser());
            crtArtist.getTopFans().put(crtUser.getUser(), count + 1);
        } else {
            //  Add the song if it's the first encounter
            crtArtist.getTopFans().put(crtUser.getUser(), 1);
        }
        //  Listeners
        if (!crtArtist.getListeners().contains(crtUser.getUser())) {
            crtArtist.getListeners().add(crtUser.getUser());
        }
    }
}
