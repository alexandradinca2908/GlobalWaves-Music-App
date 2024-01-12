package main.selectionclasses.playlists;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.monetization.PremiumUser;
import main.playlistclasses.Album;
import main.playlistclasses.Playlist;
import main.selectionclasses.ItemSelection;
import main.visitorpattern.visitorobjectnode.VisitableObjectNode;
import main.visitorpattern.visitorobjectnode.VisitorObjectNode;
import main.visitorpattern.visitorstring.VisitableString;
import main.visitorpattern.visitorstring.VisitorString;

import java.util.ArrayList;

public final class PlaylistSelection
        extends ItemSelection
        implements VisitableString, VisitableObjectNode {
    private Playlist playlist;
    private ArrayList<SongInput> shuffledPlaylist = new ArrayList<>();
    private int startTimestamp;
    private int stopTimestamp;
    public PlaylistSelection() {
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(final Playlist playlist) {
        this.playlist = playlist;
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

    public ArrayList<SongInput> getShuffledPlaylist() {
        return shuffledPlaylist;
    }

    public void setShuffledPlaylist(final ArrayList<SongInput> shuffledPlaylist) {
        this.shuffledPlaylist = shuffledPlaylist;
    }

    @Override
    public void updateRemainingTime(final int crtTimestamp,
                                    final ArrayList<Album> albums,
                                    final ArrayList<PremiumUser> premiumUsers) {
        if (!isPaused()) {
            int remainingTime = this.getRemainingTime() - (crtTimestamp - this.getStartTime());

            if (remainingTime < 0) {
                if (this.getRepeat().equals("Repeat All")) {
                    //  Count replays
                    int replays = 0;

                    //  Update time
                    while (remainingTime < 0) {
                        remainingTime += this.getPlaylist().getDuration();
                        replays++;
                    }
                    int prevRemainingTime = this.getRemainingTime();

                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  Update Wrapped
                    //  First add the songs before any replay
                    int duration = this.getPlaylist().getDuration();

                    ArrayList<SongInput> crtPlaylist = null;
                    if (!this.isShuffle()) {
                        crtPlaylist = this.getPlaylist().getSongs();
                    } else {
                        crtPlaylist = this.getShuffledPlaylist();
                    }

                    for (SongInput song : crtPlaylist) {
                        if (duration < prevRemainingTime
                                && crtPlaylist.indexOf(song) != 0) {
                            this.updateWrappedForSong(song, albums, premiumUsers);
                        }
                        duration -= song.getDuration();
                    }

                    //  Now add all the songs if there is more than one replay
                    while (replays > 1) {
                        for (SongInput song : crtPlaylist) {
                            this.updateWrappedForSong(song, albums, premiumUsers);
                        }
                        replays--;
                    }

                    //  Lastly add the songs from crt replay
                    duration = this.getPlaylist().getDuration();

                    for (SongInput song : crtPlaylist) {
                        if (duration >= remainingTime) {
                            this.updateWrappedForSong(song, albums, premiumUsers);
                        } else {
                            break;
                        }
                        duration -= song.getDuration();
                    }

                } else if (this.getRepeat().equals("Repeat Current Song")) {
                    //  Keep track of replays
                    int replays = 0;

                    //  Go back to initial timestamp
                    while (remainingTime <= this.stopTimestamp) {
                        remainingTime += (this.startTimestamp - this.stopTimestamp);
                        replays++;
                    }
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  Update Wrapped
                    //  Find crt song
                    int duration = this.getPlaylist().getDuration();
                    SongInput crtSong = null;

                    ArrayList<SongInput> crtPlaylist = null;
                    if (!this.isShuffle()) {
                        crtPlaylist = this.getPlaylist().getSongs();
                    } else {
                        crtPlaylist = this.getShuffledPlaylist();
                    }

                    for (SongInput song : crtPlaylist) {
                        duration -= song.getDuration();

                        if (duration < remainingTime) {
                            crtSong = song;
                            break;
                        }
                    }

                    while (replays > 0) {
                        this.updateWrappedForSong(crtSong, albums, premiumUsers);
                        replays--;
                    }
                } else {
                    //  Update Wrapped
                    int duration = this.getPlaylist().getDuration();
                    int prevRemainingTime = this.getRemainingTime();

                    ArrayList<SongInput> crtPlaylist = null;
                    if (!this.isShuffle()) {
                        crtPlaylist = this.getPlaylist().getSongs();
                    } else {
                        crtPlaylist = this.getShuffledPlaylist();
                    }

                    for (SongInput song : crtPlaylist) {
                        if (duration < prevRemainingTime
                                && crtPlaylist.indexOf(song) != 0) {
                            this.updateWrappedForSong(song, albums, premiumUsers);
                        }
                        duration -= song.getDuration();
                    }

                    //  Stop playlist
                    this.setRemainingTime(0);
                    this.setPaused(true);
                }

            } else {
                if (this.getRepeat().equals("Repeat Current Song")) {
                    //  Keep track of replays
                    int replays = 0;

                    //  Go back to initial timestamp
                    while (remainingTime <= this.stopTimestamp) {
                        remainingTime += (this.startTimestamp - this.stopTimestamp);
                        replays++;
                    }
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  Update Wrapped
                    //  Find crt song
                    int duration = this.getPlaylist().getDuration();
                    SongInput crtSong = null;

                    ArrayList<SongInput> crtPlaylist = null;
                    if (!this.isShuffle()) {
                        crtPlaylist = this.getPlaylist().getSongs();
                    } else {
                        crtPlaylist = this.getShuffledPlaylist();
                    }

                    for (SongInput song : crtPlaylist) {
                        duration -= song.getDuration();

                        if (duration < remainingTime) {
                            crtSong = song;
                            break;
                        }
                    }

                    while (replays > 0) {
                        this.updateWrappedForSong(crtSong, albums, premiumUsers);
                        replays--;
                    }

                } else {
                    //  Update Wrapped
                    int prevRemainingTime = this.getRemainingTime();

                    //  Update timestamps
                    this.setRemainingTime(remainingTime);
                    this.setStartTime(crtTimestamp);

                    //  If the song hasn't finished since last update
                    //  We won't add it to the database
                    int duration = this.getPlaylist().getDuration();
                    boolean skipWrapped = false;

                    ArrayList<SongInput> crtPlaylist = null;
                    if (!this.isShuffle()) {
                        crtPlaylist = this.getPlaylist().getSongs();
                    } else {
                        crtPlaylist = this.getShuffledPlaylist();
                    }

                    //  Check song
                    for (SongInput song : crtPlaylist) {
                        if (duration > this.getRemainingTime()
                                && duration < prevRemainingTime) {
                            break;
                        }
                        if (duration <= this.getRemainingTime()
                                && duration <= prevRemainingTime) {
                            skipWrapped = true;
                            break;
                        }

                        duration -= song.getDuration();
                    }

                    if (!skipWrapped) {
                        duration = this.getPlaylist().getDuration();
                        for (SongInput song : crtPlaylist) {
                            if (duration <= prevRemainingTime) {
                                if (duration < this.getRemainingTime()) {
                                    break;
                                } else {
                                    if (crtPlaylist.indexOf(song) != 0) {
                                        this.updateWrappedForSong(song, albums,
                                                premiumUsers);
                                    }
                                }
                            }
                            duration -= song.getDuration();
                        }
                    }
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

    /**
     * This method sets the interval of the current song within a playlist
     *
     * @param playlist The specified playlist
     */
    public static void setIntervalsShuffle(final PlaylistSelection playlist) {
        int remainingTime = playlist.getRemainingTime();
        int duration = playlist.getPlaylist().getDuration();

        //  Now we find the song that needs repetition
        for (SongInput song : playlist.getShuffledPlaylist()) {
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
