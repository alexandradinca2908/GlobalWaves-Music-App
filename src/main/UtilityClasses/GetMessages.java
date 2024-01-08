package main.UtilityClasses;

import fileio.input.UserInput;
import fileio.input.SongInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.EpisodeInput;
import main.CommandHelper.Search;
import main.CreatorClasses.ArtistClasses.Event;
import main.CreatorClasses.ArtistClasses.Management;
import main.CreatorClasses.ArtistClasses.Merch;
import main.CommandHelper.Command;
import main.CreatorClasses.HostClasses.Announcement;
import main.CreatorClasses.HostClasses.HostInfo;
import main.PagingClasses.Page;
import main.PlaylistClasses.Album;
import main.PlaylistClasses.Playlist;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.Playlists.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.LikeClasses.SongLikes;
import main.PlaylistClasses.UserPlaylists;
import main.VisitorPattern.VisitorString.StringClasses.VisitDeleteUser;
import main.VisitorPattern.VisitorString.StringClasses.VisitNext;
import main.VisitorPattern.VisitorString.StringClasses.VisitPrev;
import main.VisitorPattern.VisitorString.VisitorString;
import main.WrappedDatabase.StatsFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static main.Main.updatePlayer;

public final class GetMessages {

    private GetMessages() {
    }

    /**
     * This method shuffles/unshuffles the songs
     *
     * @param copyItem The cast copy of the playlist that needs shuffle
     * @param crtCommand The shuffle command with all its data
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getShuffleMessage(final PlaylistSelection copyItem,
                                           final Command crtCommand) {
        String message;

        //  Shuffle
        if (!copyItem.isShuffle()) {
            //  Keep track of the current song
            SongInput crtSong = null;
            int duration = copyItem.getPlaylist().getDuration();
            int timeLeft;

            for (SongInput song : copyItem.getPlaylist().getSongs()) {
                duration -= song.getDuration();

                if (duration < copyItem.getRemainingTime()) {
                    crtSong = song;
                    break;
                }
            }

            //  Calculate how much time is left of the current song
            timeLeft = copyItem.getRemainingTime() - duration;

            //  Shuffle the songs;
            copyItem.getShuffledPlaylist().clear();

            copyItem.getShuffledPlaylist().addAll(copyItem.getPlaylist().getSongs());
            Collections.shuffle(copyItem.getShuffledPlaylist(),
                    new Random(crtCommand.getSeed()));

            //  Search the song that is currently playing
            //  Calculate remaining time considering new order
            int remainingTime = copyItem.getPlaylist().getDuration();
            for (SongInput song : copyItem.getShuffledPlaylist()) {
                remainingTime -= song.getDuration();

                if (song.equals(crtSong)) {
                    remainingTime += timeLeft;
                    break;
                }
            }

            //  Set new remaining time
            copyItem.setRemainingTime(remainingTime);
            copyItem.setStartTime(crtCommand.getTimestamp());

            //  If the repeat status is "Repeat Current Song", intervals must be updated
            PlaylistSelection.setIntervalsShuffle(copyItem);

            //  Set the output message
            message = "Shuffle function activated successfully.";

            //  Set shuffle status
            copyItem.setShuffle(true);

            //  Unshuffle
        } else {
            //  Keep track of the current song
            SongInput crtSong = null;
            int duration = copyItem.getPlaylist().getDuration();
            int timeLeft;

            for (SongInput song : copyItem.getShuffledPlaylist()) {
                duration -= song.getDuration();

                if (duration < copyItem.getRemainingTime()) {
                    crtSong = song;
                    break;
                }
            }

            //  Calculate how much time is left of the current song
            timeLeft = copyItem.getRemainingTime() - duration;

            //  Search the song that is currently playing
            //  Calculate remaining time considering new order
            int remainingTime = copyItem.getPlaylist().getDuration();
            for (SongInput song : copyItem.getPlaylist().getSongs()) {
                remainingTime -= song.getDuration();

                if (song.equals(crtSong)) {
                    remainingTime += timeLeft;
                    break;
                }
            }

            //  Set new remaining time
            copyItem.setRemainingTime(remainingTime);
            copyItem.setStartTime(crtCommand.getTimestamp());

            //  If the repeat status is "Repeat Current Song", intervals must be updated
            PlaylistSelection.setIntervals(copyItem);

            //  Set the output message
            message = "Shuffle function deactivated successfully.";

            //  Set shuffle status
            copyItem.setShuffle(false);
        }

        return message;
    }

    /**
     * This method overloads the above method; same implementation
     *
     * @param copyItem The cast copy of the album that needs shuffle
     * @param crtCommand The shuffle command with all its data
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getShuffleMessage(final AlbumSelection copyItem,
                                           final Command crtCommand) {
        String message;

        //  Shuffle
        if (!copyItem.isShuffle()) {
            //  Keep track of the current song
            SongInput crtSong = null;
            int duration = copyItem.getAlbum().getDuration();
            int timeLeft;

            for (SongInput song : copyItem.getAlbum().getSongs()) {
                duration -= song.getDuration();

                if (duration < copyItem.getRemainingTime()) {
                    crtSong = song;
                    break;
                }
            }

            //  Calculate how much time is left of the current song
            timeLeft = copyItem.getRemainingTime() - duration;

            //  Shuffle the songs;
            copyItem.getShuffledAlbum().clear();

            copyItem.getShuffledAlbum().addAll(copyItem.getAlbum().getSongs());
            Collections.shuffle(copyItem.getShuffledAlbum(),
                    new Random(crtCommand.getSeed()));

            //  Search the song that is currently playing
            //  Calculate remaining time considering new order
            int remainingTime = copyItem.getAlbum().getDuration();
            for (SongInput song : copyItem.getShuffledAlbum()) {
                remainingTime -= song.getDuration();

                if (song.equals(crtSong)) {
                    remainingTime += timeLeft;
                    break;
                }
            }

            //  Set new remaining time
            copyItem.setRemainingTime(remainingTime);
            copyItem.setStartTime(crtCommand.getTimestamp());

            //  If the repeat status is "Repeat Current Song", intervals must be updated
            AlbumSelection.setIntervalsShuffle(copyItem);

            //  Set the output message
            message = "Shuffle function activated successfully.";

            //  Set shuffle status
            copyItem.setShuffle(true);

            //  Unshuffle
        } else {
            //  Keep track of the current song
            SongInput crtSong = null;
            int duration = copyItem.getAlbum().getDuration();
            int timeLeft;

            for (SongInput song : copyItem.getShuffledAlbum()) {
                duration -= song.getDuration();

                if (duration < copyItem.getRemainingTime()) {
                    crtSong = song;
                    break;
                }
            }

            //  Calculate how much time is left of the current song
            timeLeft = copyItem.getRemainingTime() - duration;

            //  Search the song that is currently playing
            //  Calculate remaining time considering new order
            int remainingTime = copyItem.getAlbum().getDuration();
            for (SongInput song : copyItem.getAlbum().getSongs()) {
                remainingTime -= song.getDuration();

                if (song.equals(crtSong)) {
                    remainingTime += timeLeft;
                    break;
                }
            }

            //  Set new remaining time
            copyItem.setRemainingTime(remainingTime);
            copyItem.setStartTime(crtCommand.getTimestamp());

            //  If the repeat status is "Repeat Current Song", intervals must be updated
            AlbumSelection.setIntervals(copyItem);

            //  Set the output message
            message = "Shuffle function deactivated successfully.";

            //  Set shuffle status
            copyItem.setShuffle(false);
        }

        return message;
    }

    /**
     * This method selects one item from the last search
     *
     * @param crtSearch The variable containing search data
     * @param crtCommand The select command with all its data
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getSelectMessage(final Search crtSearch,
                                          final Command crtCommand) {
        String message;
        if (crtSearch == null) {
            message = "Please conduct a search before making a selection.";
        } else if (crtCommand.getItemNumber() > crtSearch.getLastSearchResult().size() - 1) {
            message = "The selected ID is too high.";
        } else if (crtSearch.getLastSearchResult().get(0).equals("artist")
                || crtSearch.getLastSearchResult().get(0).equals("host")) {
            int index = crtCommand.getItemNumber();
            message = "Successfully selected "
                    + crtSearch.getLastSearchResult().get(index) + "'s page.";
        } else {
            //  Selecting something to play
            int index = crtCommand.getItemNumber();
            message = "Successfully selected "
                    + crtSearch.getLastSearchResult().get(index) + ".";
        }

        return message;
    }

    /**
     * This method adds the loaded song to a playlist
     *
     * @param player The array that keeps all user players in check
     * @param playlists The array of all user playlists
     * @param crtCommand The addRemoveSong command with all its data
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getAddRemoveMessage(final ArrayList<ItemSelection> player,
                                             final ArrayList<Playlist> playlists,
                                             final Command crtCommand) {
        String message;

        //  First we check to see if the user has anything loaded
        //  The loaded media MUST be a song
        int loaded = 0;
        int isSong = 0;
        int isAlbum = 0;
        AlbumSelection crtAlbum = null;
        SongInput crtSong = null;
        Playlist copyPlaylist = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                loaded = 1;
                if (item instanceof SongSelection) {
                    isSong = 1;
                    crtSong = ((SongSelection) item).getSong();
                }
                if (item instanceof AlbumSelection) {
                    isAlbum = 1;
                    crtAlbum = (AlbumSelection) item;
                }
                break;
            }
        }

        if (loaded == 0) {
            message = "Please load a source before adding to or removing from the playlist.";

        } else if (isSong == 0 && isAlbum == 0) {
            message = "The loaded source is not a song.";

        } else {
            //  We try to find the specified ID

            //  searchId will count all user playlists until we reach desired playlist
            int searchId = 0;

            for (Playlist playlist : playlists) {
                if (playlist.getOwner().equals(crtCommand.getUsername())) {
                    searchId++;
                }

                if (searchId == crtCommand.getPlaylistId()) {
                    copyPlaylist = playlist;
                    break;
                }
            }

            if (copyPlaylist == null) {
                message = "The specified playlist does not exist.";

            } else {
                //  If we are listening to an album we extract the song first
                if (isAlbum == 1) {
                    //  We need to calculate which song we are currently at and store it
                    int duration = crtAlbum.getAlbum().getDuration();

                    //  Calculating based on current time
                    crtAlbum.updateRemainingTime(crtCommand.getTimestamp());

                    for (SongInput song : crtAlbum.getAlbum().getSongs()) {
                        duration -= song.getDuration();

                        if (duration <= crtAlbum.getRemainingTime()) {
                            crtSong = song;
                            break;
                        }
                    }
                }

                //  All conditions are met; we can now add/remove the loaded song
                int foundSong = 0;
                for (SongInput song : copyPlaylist.getSongs()) {
                    if (song.equals(crtSong)) {
                        copyPlaylist.getSongs().remove(song);
                        foundSong = 1;
                        break;
                    }
                }

                //  If the song was not found, it can be added
                if (foundSong == 0) {
                    copyPlaylist.getSongs().add(crtSong);
                }

                //  Set the message
                if (foundSong == 0) {
                    message = "Successfully added to playlist.";
                } else {
                    message = "Successfully removed from playlist.";
                }
            }
        }

        return message;
    }

    /**
     * This method likes or dislikes a song
     *
     * @param player The array that keeps all user players in check
     * @param usersPlaylists The array of users and their respective playlists
     * @param crtCommand The like command with all its data
     * @param songsLikes The array of songs and their respective likes
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getLikeMessage(final ArrayList<ItemSelection> player,
                                        final ArrayList<UserPlaylists> usersPlaylists,
                                        final Command crtCommand,
                                        final ArrayList<SongLikes> songsLikes) {
        String message;

        //  We begin by checking if there is a loaded source, the source MUST be a song
        int loaded = 0;
        int isSong = 0;
        int isPlaylist = 0;
        int isAlbum = 0;
        SongInput crtSong = null;
        PlaylistSelection crtPlaylist = null;
        AlbumSelection crtAlbum = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                loaded = 1;
                if (item instanceof SongSelection) {
                    isSong = 1;
                    crtSong = ((SongSelection) item).getSong();
                } else if (item instanceof PlaylistSelection) {
                    isPlaylist = 1;
                    crtPlaylist = (PlaylistSelection) item;
                } else if (item instanceof AlbumSelection) {
                    isAlbum = 1;
                    crtAlbum = (AlbumSelection) item;
                }
                break;
            }
        }

        if (loaded == 0) {
            message = "Please load a source before liking or unliking.";

        } else if (isSong == 0 && isPlaylist == 0 && isAlbum == 0) {
            message = "Loaded source is not a song.";

        } else if (isSong == 1) {
            //  The loaded source is checked. We can add/remove it from liked songs
            UserPlaylists user = null;

            //  Find user
            for (UserPlaylists crtUser : usersPlaylists) {
                if (crtUser.getUser().getUsername().equals(crtCommand.getUsername())) {
                    user = crtUser;
                    break;
                }
            }

            if (user != null) {
                //  We search the current song
                int found = 0;
                for (SongInput song : user.getLikedSongs()) {
                    if (song.equals(crtSong)) {
                        found = 1;
                        user.getLikedSongs().remove(song);

                        break;
                    }
                }

                if (found == 0) {
                    //  Add like
                    user.getLikedSongs().add(crtSong);
                    for (SongLikes song : songsLikes) {
                        if (song.getSong().equals(crtSong)) {
                            song.setLikes(song.getLikes() + 1);
                            break;
                        }
                    }

                    message = "Like registered successfully.";

                } else {
                    //  Also remove like from songsLikes
                    for (SongLikes song : songsLikes) {
                        if (song.getSong().equals(crtSong)) {
                            song.setLikes(song.getLikes() - 1);
                            break;
                        }
                    }

                    message = "Unlike registered successfully.";
                }
            } else {
                message = "ERROR. User not found";
            }
        } else if (isPlaylist == 1) {
            //  We need to calculate which song we are currently at and store it
            SongInput crtSongInPlaylist = null;
            int duration = crtPlaylist.getPlaylist().getDuration();

            //  Calculating based on current time
            crtPlaylist.updateRemainingTime(crtCommand.getTimestamp());

            //  Choosing the appropriate order considering shuffle status
            ArrayList<SongInput> songOrder;
            if (!crtPlaylist.isShuffle()) {
                songOrder = crtPlaylist.getPlaylist().getSongs();
            } else {
                songOrder = crtPlaylist.getShuffledPlaylist();
            }

            for (SongInput song : songOrder) {
                duration -= song.getDuration();

                if (duration < crtPlaylist.getRemainingTime()) {
                    crtSongInPlaylist = song;
                    break;
                }
            }

            //  The loaded song is checked. We can add/remove it from liked songs
            UserPlaylists user = null;

            for (UserPlaylists crtUser : usersPlaylists) {
                if (crtUser.getUser().getUsername().equals(crtCommand.getUsername())) {
                    user = crtUser;
                    break;
                }
            }

            if (user != null) {
                //  We search the current song
                int found = 0;
                for (SongInput song : user.getLikedSongs()) {
                    if (song.equals(crtSongInPlaylist)) {
                        found = 1;
                        user.getLikedSongs().remove(song);

                        break;
                    }
                }

                if (found == 0) {
                    //  Add like
                    user.getLikedSongs().add(crtSongInPlaylist);
                    for (SongLikes song : songsLikes) {
                        if (song.getSong().equals(crtSongInPlaylist)) {
                            song.setLikes(song.getLikes() + 1);
                            break;
                        }
                    }

                    message = "Like registered successfully.";
                } else {
                    //  Also remove the like from the song
                    for (SongLikes song : songsLikes) {
                        if (song.getSong().equals(crtSongInPlaylist)) {
                            song.setLikes(song.getLikes() - 1);
                            break;
                        }
                    }

                    message = "Unlike registered successfully.";
                }

            } else {
                message = "ERROR. User not found";
            }

        } else {
            //  We need to calculate which song we are currently at and store it
            SongInput crtSongInAlbum = null;
            int duration = crtAlbum.getAlbum().getDuration();

            //  Calculating based on current time
            crtAlbum.updateRemainingTime(crtCommand.getTimestamp());

            //  Choosing the appropriate order considering shuffle status
            ArrayList<SongInput> songOrder;
            if (!crtAlbum.isShuffle()) {
                songOrder = crtAlbum.getAlbum().getSongs();
            } else {
                songOrder = crtAlbum.getShuffledAlbum();
            }

            for (SongInput song : songOrder) {
                duration -= song.getDuration();

                if (duration < crtAlbum.getRemainingTime()) {
                    crtSongInAlbum = song;
                    break;
                }
            }

            //  The loaded song is checked. We can add/remove it from liked songs
            UserPlaylists user = null;

            for (UserPlaylists crtUser : usersPlaylists) {
                if (crtUser.getUser().getUsername().equals(crtCommand.getUsername())) {
                    user = crtUser;
                    break;
                }
            }

            if (user != null) {
                //  We search the current song
                int found = 0;
                for (SongInput song : user.getLikedSongs()) {
                    if (song.equals(crtSongInAlbum)) {
                        found = 1;
                        user.getLikedSongs().remove(song);

                        break;
                    }
                }

                if (found == 0) {
                    //  Add like
                    user.getLikedSongs().add(crtSongInAlbum);
                    for (SongLikes song : songsLikes) {
                        if (song.getSong().equals(crtSongInAlbum)) {
                            song.setLikes(song.getLikes() + 1);
                            break;
                        }
                    }

                    message = "Like registered successfully.";
                } else {
                    //  Also remove the like from the song
                    for (SongLikes song : songsLikes) {
                        if (song.getSong().equals(crtSongInAlbum)) {
                            song.setLikes(song.getLikes() - 1);
                            break;
                        }
                    }

                    message = "Unlike registered successfully.";
                }

            } else {
                message = "ERROR. User not found";
            }
        }

        return message;
    }

    /**
     * This method skips 90 seconds of the current episode or moves to the next episode
     *
     * @param crtItem The loaded item in the users player
     * @param crtCommand The forward command with all its data
     * @param player The array that keeps all user players in check
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getForwardMessage(final ItemSelection crtItem, final Command crtCommand,
                                           final ArrayList<ItemSelection> player,
                                           final ArrayList<PodcastSelection> podcasts) {
        String message;

        //  Verify if the command is possible
        if (crtItem == null) {
            message = "Please load a source before attempting to forward.";
        } else {
            if (!(crtItem instanceof PodcastSelection copyItem)) {
                message = "The loaded source is not a podcast.";
            } else {
                //  We can now forward the podcast 90 seconds

                //  Find the current episode
                EpisodeInput crtEp = null;
                int duration = copyItem.getPodcast().getDuration();

                for (EpisodeInput episode : copyItem.getPodcast().getEpisodes()) {
                    duration -= episode.getDuration();

                    if (duration < copyItem.getRemainingTime()) {
                        crtEp = episode;
                        break;
                    }
                }

                //  Forward
                copyItem.setRemainingTime(crtItem.getRemainingTime()
                        - Constants.FORWARD_TIME);

                //  If the podcast finished, we update the time
                if (copyItem.getRemainingTime() < 0) {
                    copyItem.updateRemainingTime(crtCommand.getTimestamp());
                    player.remove(copyItem);
                    podcasts.remove(copyItem);

                    //  If it didn't, we check to see if we jumped to the next episode
                } else {
                    duration = copyItem.getPodcast().getDuration();

                    for (EpisodeInput episode : copyItem.getPodcast().getEpisodes()) {
                        duration -= episode.getDuration();

                        if (duration < copyItem.getRemainingTime()) {
                            //  Compare episodes
                            //  If we went to the next episode, start from beginning
                            if (!episode.equals(crtEp)) {
                                duration += episode.getDuration();
                                copyItem.setRemainingTime(duration);
                            }
                            break;
                        }
                    }
                }
                message = "Skipped forward successfully.";
            }
        }

        return message;
    }

    /**
     * This method goes back 90 seconds of the current episode or moves to the previous episode
     *
     * @param crtItem The loaded item in the users player
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getBackwardMessage(final ItemSelection crtItem) {
        String message;

        //  Verify if the command is possible
        if (crtItem == null) {
            message = "Please select a source before rewinding.";
        } else {
            if (!(crtItem instanceof PodcastSelection copyItem)) {
                message = "The loaded source is not a podcast.";
            } else {
                //  We can now backward the podcast 90 seconds

                //  Find the current episode
                EpisodeInput crtEp = null;
                int duration = copyItem.getPodcast().getDuration();

                for (EpisodeInput episode : copyItem.getPodcast().getEpisodes()) {
                    duration -= episode.getDuration();

                    if (duration < copyItem.getRemainingTime()) {
                        crtEp = episode;
                        break;
                    }
                }

                //  Backward
                copyItem.setRemainingTime(crtItem.getRemainingTime()
                        + Constants.BACKWARD_TIME);

                //  Check to see if we jumped back to the start
                duration = copyItem.getPodcast().getDuration();

                if (copyItem.getRemainingTime() > duration) {
                    copyItem.setRemainingTime(duration);
                } else {
                    for (EpisodeInput episode : copyItem.getPodcast().getEpisodes()) {
                        duration -= episode.getDuration();

                        if (duration < copyItem.getRemainingTime()) {
                            //  Compare episodes
                            //  If we went to the next episode, start from beginning
                            if (!episode.equals(crtEp)) {
                                duration += episode.getDuration();
                                copyItem.setRemainingTime(duration);
                            }
                            break;
                        }
                    }
                }
                message = "Rewound successfully.";
            }
        }

        return message;
    }

    /**
     * This method skips to the next track
     *
     * @param crtItem The loaded item in the users player
     * @param player The array that keeps all user players in check
     * @param podcasts The array that keeps track of all the podcasts
     *                 when they are not loaded
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getNextMessage(final ItemSelection crtItem,
                                        final ArrayList<ItemSelection> player,
                                        final ArrayList<PodcastSelection> podcasts,
                                        final Command crtCommand) {
        String message = "";

        //  Verify if the command is possible
        if (crtItem == null) {
            message = "Please load a source before skipping to the next track.";
        } else {
            //  Now we can execute the command
            VisitorString visitNextMessage = new VisitNext(player,
                    podcasts, crtCommand);

            message = crtItem.acceptString(visitNextMessage);
        }

        return message;
    }

    /**
     * This method goes back to the previous track
     *
     * @param crtItem The loaded item in the users player
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getPrevMessage(final ItemSelection crtItem,
                                        final Command crtCommand) {
        String message = "";

        //  Verify if the command is possible
        if (crtItem == null) {
            message = "Please load a source before returning to the previous track.";
        } else {
            //  Now we can execute the command
            VisitorString visitPrevMessage = new VisitPrev(crtCommand);

            message = crtItem.acceptString(visitPrevMessage);
        }

        return message;
    }

    /**
     * This method helps the user follow/unfollow a playlist
     *
     * @param wantedPlaylist The playlist selected by the user
     * @param crtCommand The follow command with all its data
     * @param usersPlaylists The array of users and their respective playlists
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getFollowMessage(final Playlist wantedPlaylist,
                                          final Command crtCommand,
                                          final ArrayList<UserPlaylists> usersPlaylists) {
        String message;

        //  Begin by checking whether the user follows this playlist or not
        int found = 0;
        for (String user : wantedPlaylist.getFollowers()) {
            if (user.equals(crtCommand.getUsername())) {
                //  Remove follower
                wantedPlaylist.getFollowers().remove(user);
                found = 1;
                break;
            }
        }

        //  Remove playlist from user's followed playlists
        if (found == 1) {
            for (UserPlaylists userPlaylists : usersPlaylists) {
                if (userPlaylists.getUser().getUsername().equals(crtCommand.getUsername())) {
                    userPlaylists.getFollowedPlaylists().remove(wantedPlaylist);
                    break;
                }
            }
            message = "Playlist unfollowed successfully.";

        } else {
            //  Add the user to the playlist followers
            wantedPlaylist.getFollowers().add(crtCommand.getUsername());

            //  Add the playlist to the user's followed playlists
            for (UserPlaylists userPlaylists : usersPlaylists) {
                if (userPlaylists.getUser().getUsername().equals(crtCommand.getUsername())) {
                    userPlaylists.getFollowedPlaylists().add(wantedPlaylist);
                    break;
                }
            }

            message = "Playlist followed successfully.";
        }

        return message;
    }

    /**
     * This method switches the visibility of a playlist
     *
     * @param usersPlaylists The array of users and their respective playlists
     * @param crtCommand The switchVisibility command with all its data
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getSwitchVisibilityMessage(final ArrayList<UserPlaylists> usersPlaylists,
                                                    final Command crtCommand) {
        String message;

        //  We find the user, search through the playlists and switch the visibility

        //  Find user
        UserPlaylists crtUser = null;

        for (UserPlaylists userPlaylists : usersPlaylists) {
            if (userPlaylists.getUser().getUsername().equals(crtCommand.getUsername())) {
                crtUser = userPlaylists;
                break;
            }
        }

        if (crtCommand.getPlaylistId() > crtUser.getPlaylists().size()) {
            //  ID too high
            message = "The specified playlist ID is too high.";
        } else {
            //  Search playlist
            Playlist crtPlaylist = null;
            int searchId = 0;

            for (Playlist playlist : crtUser.getPlaylists()) {
                searchId++;

                if (searchId == crtCommand.getPlaylistId()) {
                    crtPlaylist = playlist;
                    break;
                }
            }

            //  Switch visibility
            crtPlaylist.setVisibility(!crtPlaylist.isVisibility());
            if (crtPlaylist.isVisibility()) {
                message = "Visibility status updated successfully to public.";
            } else {
                message = "Visibility status updated successfully to private.";
            }
        }


        return message;
    }

    /**
     * This method switches the connection (online/offline) of a user
     *
     * @param crtUser Current user
     * @param player The array that keeps all user players in check
     * @param crtCommand The switchConnectionStatus command with all its data
     * @param podcasts The array that keeps track of all the podcasts
     *                 when they are not loaded
     * @param library Singleton containing all songs, users and podcasts
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getSwitchConnectionMessage(final UserInput crtUser,
                                                    final ArrayList<ItemSelection> player,
                                                    final Command crtCommand,
                                                    final ArrayList<PodcastSelection> podcasts,
                                                    final LibraryInput library) {
        String message;

        if (crtUser == null) {
            message = "The username " + crtCommand.getUsername() + " doesn't exist.";
        } else if (!crtUser.getType().equals("user")) {
            message = crtCommand.getUsername() + " is not a normal user.";
        } else {
            //  The user exists and is normal. Status can be updated

            //  First we update timestamps
            updatePlayer(player, crtCommand, podcasts, library);

            //  Then we set the user's start time from where he left off
            if (!crtUser.isOnline()) {
                for (ItemSelection item : player) {
                    if (item.getUser().equals(crtUser.getUsername())) {
                        item.setStartTime(crtCommand.getTimestamp());
                        break;
                    }
                }
            }

            //  Switch status
            crtUser.setOnline(!crtUser.isOnline());

            message = crtUser.getUsername() + " has changed status successfully.";
        }

        return message;
    }

    /**
     * This method adds a new user
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param usersPlaylists The array of users and their respective playlists
     * @param pageSystem The array of user pages
     * @param managements The array of managing technicalities for artists
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getAddUserMessage(final Command crtCommand,
                                           final LibraryInput library,
                                           final ArrayList<UserPlaylists> usersPlaylists,
                                           final ArrayList<Page> pageSystem,
                                           final ArrayList<Management> managements,
                                           final ArrayList<HostInfo> hostInfos) {
        String message;

        //  Search to see if this is a new user
        boolean isTaken = false;
        String wantedUsername = crtCommand.getUsername();

        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(wantedUsername)) {
                isTaken = true;
                break;
            }
        }

        if (isTaken) {
            message = "The username " + wantedUsername + " is already taken.";
        } else {
            //  Create user
            UserInput newUser = new UserInput();

            //  Set user data
            newUser.setType(crtCommand.getType());
            newUser.setUsername(crtCommand.getUsername());
            newUser.setAge(crtCommand.getAge());
            newUser.setCity(crtCommand.getCity());
            newUser.setOnline(true);

            //  Add user in all databases
            //  Library
            library.getUsers().add(newUser);

            //  Users' Playlists
            UserPlaylists newUserPlaylists = new UserPlaylists();
            newUserPlaylists.setUser(newUser);
            usersPlaylists.add(newUserPlaylists);

            //  Page system
            Page newPage = new Page();
            newPage.setUserPlaylists(newUserPlaylists);
            newPage.setPageOwner(newUserPlaylists.getUser());
            pageSystem.add(newPage);

            //  Wrapped Statistics
            StatsFactory.createStats(newUser);

            //  Management, if the user is an artist
            if (newUser.getType().equals("artist")) {
                Management newManagement = new Management();
                newManagement.setArtist(newUser);
                managements.add(newManagement);
                newPage.setManagement(newManagement);
            }

            //  Host Info, if the user is a host
            if (newUser.getType().equals("host")) {
                HostInfo newHostInfo = new HostInfo();
                newHostInfo.setHost(newUser);
                hostInfos.add(newHostInfo);
                newPage.setHostInfo(newHostInfo);
            }

            message = "The username " + wantedUsername + " has been added successfully.";
        }

        return message;
    }

    /**
     * This method adds a new album
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param usersPlaylists The array of users and their respective playlists
     * @param albums The array of all albums in the database
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getAddAlbumMessage(final Command crtCommand,
                                            final LibraryInput library,
                                            final ArrayList<UserPlaylists> usersPlaylists,
                                            final ArrayList<Album> albums,
                                            final ArrayList<SongLikes> songsLikes) {
        String message;

        UserInput artist = null;
        boolean exists = false;
        boolean isArtist = false;

        //  Checking to see artist availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("artist")) {
                    isArtist = true;
                    artist = user;
                }
                break;
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isArtist) {
            message = crtCommand.getUsername() + " is not an artist.";
        } else {
            //  Artist is eligible to add album

            //  Verify album name uniqueness
            //  First we find the user's playlists
            UserPlaylists allPlaylists = null;

            for (UserPlaylists userPlaylists : usersPlaylists) {
                if (userPlaylists.getUser().equals(artist)) {
                    allPlaylists = userPlaylists;
                    break;
                }
            }

            boolean duplicate = false;
            //  Now we check the name
            for (Album album : allPlaylists.getAlbums()) {
                if (album.getName().equals(crtCommand.getName())) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                message = crtCommand.getUsername()
                        + " has another album with the same name.";
            } else {
                //  We check to see if the album has duplicate songs
                boolean sameName = false;

                for (int i = 0; i < crtCommand.getSongs().size() - 1; i++) {
                    SongInput crtSong = crtCommand.getSongs().get(i);
                    for (int j = i + 1; j < crtCommand.getSongs().size(); j++) {
                        SongInput nextSong = crtCommand.getSongs().get(j);

                        if (crtSong.getName().equals(nextSong.getName())) {
                            sameName = true;
                            break;
                        }
                    }

                    if (sameName) {
                        break;
                    }
                }

                if (sameName) {
                    message = crtCommand.getUsername()
                            + " has the same song at least twice in this album.";
                } else {
                    //  The album can be initialized
                    Album newAlbum = new Album();

                    //  Set data
                    newAlbum.setOwner(crtCommand.getUsername());
                    newAlbum.setName(crtCommand.getName());
                    newAlbum.setReleaseYear(crtCommand.getReleaseYear());
                    newAlbum.setDescription(crtCommand.getDescription());
                    newAlbum.setSongs(crtCommand.getSongs());

                    //  Add album and songs in all databases
                    //  Artist's albums
                    allPlaylists.getAlbums().add(newAlbum);

                    //  All albums
                    albums.add(newAlbum);

                    //  Add songs in library
                    //  Add songs in songs likes
                    //  Add songs with like count in album
                    for (SongInput song : newAlbum.getSongs()) {
                        library.getSongs().add(song);

                        SongLikes newSong = new SongLikes();
                        newSong.setSong(song);

                        songsLikes.add(newSong);
                        newAlbum.getSongsWithLikes().add(newSong);
                    }

                    message = crtCommand.getUsername()
                            + " has added new album successfully.";
                }
            }
        }

        return message;
    }

    /**
     * This method adds a new event
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param managements The array of managing technicalities for artists
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getAddEventMessage(final LibraryInput library,
                                            final Command crtCommand,
                                            final ArrayList<Management> managements) {
        String message = null;

        UserInput artist = null;
        boolean exists = false;
        boolean isArtist = false;

        //  Checking to see artist availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("artist")) {
                    isArtist = true;
                    artist = user;
                }
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isArtist) {
            message = crtCommand.getUsername() + " is not an artist.";
        } else {
            //  Artist may add event

            //  We need to check event uniqueness
            boolean sameName = false;
            ArrayList<Event> allEvents = null;

            for (Management management : managements) {
                if (management.getArtist().equals(artist)) {
                    allEvents = management.getEvents();
                    break;
                }
            }

            //  Browsing through events
            for (Event event : allEvents) {
                if (event.getName().equals(crtCommand.getName())) {
                    sameName = true;
                    break;
                }
            }

            if (sameName) {
                message = crtCommand.getUsername()
                        + " has another event with the same name.";
            } else {
                //  Checking date information
                String[] date = crtCommand.getDate().split("-");
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]);
                int year = Integer.parseInt(date[2]);

                //  General date check
                if (day > Constants.LAST_DAY_OF_MONTH
                        || month > Constants.LAST_MONTH_OF_YEAR
                        || year < Constants.MIN_YEAR
                        || year > Constants.MAX_YEAR) {
                    message = "Event for " + artist.getUsername()
                            + " does not have a valid date.";
                } else {
                    //  Checking exceptions
                    if (month == Constants.FEBRUARY
                            && day > Constants.LAST_DAY_OF_FEBRUARY) {
                        message = "Event for " + artist.getUsername()
                                + " does not have a valid date.";

                        //  Date is correct; all conditions are met
                    } else {
                        Event newEvent = new Event();

                        newEvent.setDate(crtCommand.getDate());
                        newEvent.setName(crtCommand.getName());
                        newEvent.setDescription(crtCommand.getDescription());

                        //  Add event
                        allEvents.add(newEvent);

                        message = crtCommand.getUsername()
                                + " has added new event successfully.";
                    }
                }
            }
        }

        return message;
    }

    /**
     * This method adds new merch
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param managements The array of managing technicalities for artists
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getAddMerchMessage(final LibraryInput library,
                                            final Command crtCommand,
                                            final ArrayList<Management> managements) {
        String message = null;

        UserInput artist = null;
        boolean exists = false;
        boolean isArtist = false;

        //  Checking to see artist availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("artist")) {
                    isArtist = true;
                    artist = user;
                }
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isArtist) {
            message = crtCommand.getUsername() + " is not an artist.";
        } else {
            //  Artist may add merch

            //  We need to check merch uniqueness
            boolean sameName = false;
            ArrayList<Merch> allMerch = null;

            for (Management management : managements) {
                if (management.getArtist().equals(artist)) {
                    allMerch = management.getMerches();
                    break;
                }
            }

            //  Browsing through merchandise
            for (Merch merch : allMerch) {
                if (merch.getName().equals(crtCommand.getName())) {
                    sameName = true;
                    break;
                }
            }

            if (sameName) {
                message = crtCommand.getUsername()
                        + " has merchandise with the same name.";
            } else {
                //  Checking price information
                if (crtCommand.getPrice() < 0) {
                    message = "Price for merchandise can not be negative.";
                } else {
                    //  Merch can be added
                    Merch newMerch = new Merch();

                    newMerch.setName(crtCommand.getName());
                    newMerch.setDescription(crtCommand.getDescription());
                    newMerch.setPrice(crtCommand.getPrice());

                    //  Add event
                    allMerch.add(newMerch);

                    message = crtCommand.getUsername()
                            + " has added new merchandise successfully.";
                }
            }
        }

        return message;
    }

    /**
     * This method deletes a user
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param player The array that keeps all user players in check
     * @param podcasts The array that keeps track of all the podcasts
     *                 when they are not loaded
     * @param playlists The array of all user playlists
     * @param usersPlaylists The array of users and their respective playlists
     * @param albums The array of all albums in the database
     * @param songsLikes The array of songs and their respective likes
     * @param pageSystem Array of all the pages in the system
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getDeleteUserMessage(final LibraryInput library,
                                              final Command crtCommand,
                                              final ArrayList<ItemSelection> player,
                                              final ArrayList<Playlist> playlists,
                                              final ArrayList<UserPlaylists> usersPlaylists,
                                              final ArrayList<Album> albums,
                                              final ArrayList<SongLikes> songsLikes,
                                              final ArrayList<Page> pageSystem,
                                              final ArrayList<PodcastSelection> podcasts) {
        String message = null;

        //  Searching the user through database
        UserInput crtUser = null;

        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                crtUser = user;
                break;
            }
        }

        if (crtUser == null) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else {
            //  Looking to see if user's dependencies are being used
            VisitorString visitDeleteUser = new VisitDeleteUser(crtCommand);
            String used = "false";

            //  User's items
            for (ItemSelection item : player) {
                used = item.acceptString(visitDeleteUser);
                if (used.equals("true")) {
                    break;
                }
            }

            //  Creator's page
            if (crtUser.getType().equals("artist")) {
                for (Page page : pageSystem) {
                    if (page.getCurrentPage().equals("ArtistPage")
                            && page.getUserPlaylists().getUser().equals(crtUser)) {
                        used = "true";
                        break;
                    }
                }
            } else if (crtUser.getType().equals("host")) {
                for (Page page : pageSystem) {
                    if (page.getCurrentPage().equals("HostPage")
                            && page.getUserPlaylists().getUser().equals(crtUser)) {
                        used = "true";
                        break;
                    }
                }
            }

            if (used.equals("true")) {
                message = crtCommand.getUsername() + " can't be deleted.";
            } else {
                //  We can safely delete this user
                if (crtUser.getType().equals("user")) {
                    //  Collect all deletable playlists
                    ArrayList<Playlist> removables = new ArrayList<>();
                    for (Playlist playlist : playlists) {
                        if (playlist.getOwner().equals(crtUser.getUsername())) {
                            removables.add(playlist);
                        }
                    }

                    //  Before deleting playlists we must delete follows
                    for (Playlist playlist : removables) {
                        for (String username : playlist.getFollowers()) {
                            for (UserPlaylists user : usersPlaylists) {
                                if (user.getUser().getUsername().equals(username)) {
                                    user.getFollowedPlaylists().remove(playlist);
                                    break;
                                }
                            }
                        }
                        playlists.remove(playlist);
                    }

                    //  Also delete user's follows
                    for (Playlist playlist : playlists) {
                        playlist.getFollowers().remove(crtUser.getUsername());
                    }

                    //  Now delete the user from the database
                    UserPlaylists deleteUserPlaylists = null;
                    for (UserPlaylists userPlaylists : usersPlaylists) {
                        if (userPlaylists.getUser().equals(crtUser)) {
                            deleteUserPlaylists = userPlaylists;
                            break;
                        }
                    }
                    usersPlaylists.remove(deleteUserPlaylists);

                    //  Delete user's page
                    for (Page page : pageSystem) {
                        if (page.getPageOwner().getUsername()
                                .equals(crtCommand.getUsername())) {
                            pageSystem.remove(page);
                            break;
                        }
                    }

                    //  Lastly delete the user themselves
                    library.getUsers().remove(crtUser);

                    message = crtUser.getUsername() + " was successfully deleted.";
                }

                if (crtUser.getType().equals("artist")) {
                    //  Collect all deletable albums
                    ArrayList<Album> removables = new ArrayList<>();
                    for (Album album : albums) {
                        if (album.getOwner().equals(crtUser.getUsername())) {
                            removables.add(album);
                        }
                    }

                    //  By deleting albums we must delete song likes
                    for (Album album : removables) {
                        for (SongInput song : album.getSongs()) {
                            //  Delete song from users' likes
                            for (UserPlaylists user : usersPlaylists) {
                                if (user.getLikedSongs().contains(song)) {
                                    user.getLikedSongs().remove(song);
                                    break;
                                }
                            }
                            library.getSongs().remove(song);

                            //  Delete song from song likes array
                            SongLikes removableSong = null;
                            for (SongLikes songLikes : songsLikes) {
                                if (songLikes.getSong().equals(song)) {
                                    removableSong = songLikes;
                                    break;
                                }
                            }
                            if (removableSong != null) {
                                songsLikes.remove(removableSong);
                            }
                        }
                        //  Delete album
                        albums.remove(album);
                    }

                    //  Delete user's page
                    for (Page page : pageSystem) {
                        if (page.getPageOwner().getUsername()
                                .equals(crtCommand.getUsername())) {
                            pageSystem.remove(page);
                            break;
                        }
                    }

                    //  Now delete the user from the database
                    UserPlaylists deleteUserPlaylists = null;
                    for (UserPlaylists userPlaylists : usersPlaylists) {
                        if (userPlaylists.getUser().equals(crtUser)) {
                            deleteUserPlaylists = userPlaylists;
                            break;
                        }
                    }
                    usersPlaylists.remove(deleteUserPlaylists);

                    //  Lastly delete the user themselves
                    library.getUsers().remove(crtUser);

                    message = crtUser.getUsername() + " was successfully deleted.";
                }
                if (crtUser.getType().equals("host")) {
                    //  Collect all deletable podcasts
                    ArrayList<PodcastInput> removables = new ArrayList<>();
                    for (PodcastInput podcast : library.getPodcasts()) {
                        if (podcast.getOwner().equals(crtUser.getUsername())) {
                            removables.add(podcast);
                        }
                    }

                    //  Delete podcasts from library
                    for (PodcastInput podcast1 : removables) {
                        library.getPodcasts().remove(podcast1);

                        //  Delete podcasts from paused podcasts
                        for (PodcastSelection podcast2 : podcasts) {
                            if (podcast2.getPodcast().equals(podcast1)) {
                                podcasts.remove(podcast2);
                                break;
                            }
                        }
                    }

                    //  Delete user's page
                    for (Page page : pageSystem) {
                        if (page.getPageOwner().getUsername()
                                .equals(crtCommand.getUsername())) {
                            pageSystem.remove(page);
                            break;
                        }
                    }

                    //  Now delete the user from the database
                    UserPlaylists deleteUserPlaylists = null;
                    for (UserPlaylists userPlaylists : usersPlaylists) {
                        if (userPlaylists.getUser().equals(crtUser)) {
                            deleteUserPlaylists = userPlaylists;
                            break;
                        }
                    }
                    usersPlaylists.remove(deleteUserPlaylists);

                    //  Lastly delete the user themselves
                    library.getUsers().remove(crtUser);

                    message = crtUser.getUsername() + " was successfully deleted.";
                }
            }
        }

        return message;
    }

    /**
     * This method adds a new podcast
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param usersPlaylists The array of users and their respective playlists
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getAddPodcastMessage(final LibraryInput library,
                                              final Command crtCommand,
                                              final ArrayList<UserPlaylists> usersPlaylists) {
        String message = null;

        UserInput host = null;
        boolean exists = false;
        boolean isHost = false;

        //  Checking to see artist availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("host")) {
                    isHost = true;
                    host = user;
                }
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isHost) {
            message = crtCommand.getUsername() + " is not a host.";
        } else {
            //  Host is eligible to add podcast

            //  Verify podcast name uniqueness
            //  First we find the user's playlists
            UserPlaylists allPlaylists = null;

            for (UserPlaylists userPlaylists : usersPlaylists) {
                if (userPlaylists.getUser().equals(host)) {
                    allPlaylists = userPlaylists;
                    break;
                }
            }

            boolean duplicate = false;
            //  Now we check the name
            for (PodcastInput podcast : allPlaylists.getPodcasts()) {
                if (podcast.getName().equals(crtCommand.getName())) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                message = crtCommand.getUsername()
                        + " has another podcast with the same name.";
            } else {
                //  We check to see if the podcast has duplicate episodes
                boolean sameName = false;

                for (int i = 0; i < crtCommand.getEpisodes().size() - 1; i++) {
                    EpisodeInput crtEpisode = crtCommand.getEpisodes().get(i);
                    for (int j = i + 1; j < crtCommand.getEpisodes().size(); j++) {
                        EpisodeInput nextEpisode = crtCommand.getEpisodes().get(j);

                        if (crtEpisode.getName().equals(nextEpisode.getName())) {
                            sameName = true;
                            break;
                        }
                    }

                    if (sameName) {
                        break;
                    }
                }

                if (sameName) {
                    message = crtCommand.getUsername()
                            + " has the same episode in this podcast.";
                } else {
                    //  The podcast can be initialized
                    PodcastInput newPodcast = new PodcastInput();

                    //  Set data
                    newPodcast.setOwner(crtCommand.getUsername());
                    newPodcast.setName(crtCommand.getName());
                    newPodcast.setEpisodes(crtCommand.getEpisodes());

                    //  Add album and songs in all databases
                    //  Artist's podcasts
                    allPlaylists.getPodcasts().add(newPodcast);

                    //  All podcasts
                    library.getPodcasts().add(newPodcast);

                    message = crtCommand.getUsername()
                            + " has added new podcast successfully.";
                }
            }
        }

        return message;
    }

    /**
     * This method adds a new announcement
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param hostInfos The array containing all announcements for every artist
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getAddAnnouncementMessage(final LibraryInput library,
                                                   final Command crtCommand,
                                                   final ArrayList<HostInfo> hostInfos) {
        String message = null;

        UserInput host = null;
        boolean exists = false;
        boolean isHost = false;

        //  Checking to see host availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("host")) {
                    isHost = true;
                    host = user;
                }
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isHost) {
            message = crtCommand.getUsername() + " is not a host.";
        } else {
            //  Host may add announcement

            //  We need to check announcement uniqueness
            boolean sameName = false;
            ArrayList<Announcement> allAnnouncements = null;

            for (HostInfo hostInfo : hostInfos) {
                if (hostInfo.getHost().equals(host)) {
                    allAnnouncements = hostInfo.getAnnouncements();
                    break;
                }
            }

            //  Browsing through announcements
            for (Announcement announcement : allAnnouncements) {
                if (announcement.getName().equals(crtCommand.getName())) {
                    sameName = true;
                    break;
                }
            }

            if (sameName) {
                message = crtCommand.getUsername()
                        + " has already added an announcement with this name.";
            } else {
                Announcement newAnnouncement = new Announcement();

                newAnnouncement.setName(crtCommand.getName());
                newAnnouncement.setDescription(crtCommand.getDescription());

                //  Add event
                allAnnouncements.add(newAnnouncement);

                message = crtCommand.getUsername()
                        + " has successfully added new announcement.";
            }
        }

        return message;
    }

    /**
     * This method removes an announcement
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param hostInfos The array containing all announcements for every artist
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getRemoveAnnouncementMessage(final LibraryInput library,
                                                      final Command crtCommand,
                                                      final ArrayList<HostInfo> hostInfos) {
        String message = null;

        UserInput host = null;
        boolean exists = false;
        boolean isHost = false;

        //  Checking to see host availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("host")) {
                    isHost = true;
                    host = user;
                }
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isHost) {
            message = crtCommand.getUsername() + " is not a host.";
        } else {
            //  Host may remove announcement

            //  We need to check if the announcement exists
            ArrayList<Announcement> allAnnouncements = null;

            for (HostInfo hostInfo : hostInfos) {
                if (hostInfo.getHost().equals(host)) {
                    allAnnouncements = hostInfo.getAnnouncements();
                    break;
                }
            }

            //  Browsing through announcements
            Announcement removableAnnouncement = null;
            for (Announcement announcement : allAnnouncements) {
                if (announcement.getName().equals(crtCommand.getName())) {
                    removableAnnouncement = announcement;
                    break;
                }
            }

            if (removableAnnouncement == null) {
                message = crtCommand.getUsername()
                        + " has no announcement with the given name.";
            } else {
                //  We can remove announcement
                allAnnouncements.remove(removableAnnouncement);

                message = crtCommand.getUsername()
                        + " has successfully deleted the announcement.";
            }
        }

        return message;
    }

    /**
     * This method removes an album
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param usersPlaylists The array of users and their respective playlists
     * @param player The array that keeps all user players in check
     * @param playlists The array of all user playlists
     * @param songsLikes The array of songs and their respective likes
     * @param albums The array of all albums in the database
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getRemoveAlbumMessage(final LibraryInput library,
                                               final Command crtCommand,
                                               final ArrayList<UserPlaylists> usersPlaylists,
                                               final ArrayList<ItemSelection> player,
                                               final ArrayList<Playlist> playlists,
                                               final ArrayList<SongLikes> songsLikes,
                                               final ArrayList<Album> albums) {
        String message;

        UserInput artist = null;
        boolean exists = false;
        boolean isArtist = false;

        //  Checking to see artist availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("artist")) {
                    isArtist = true;
                    artist = user;
                }
                break;
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isArtist) {
            message = crtCommand.getUsername() + " is not an artist.";
        } else {
            //  Artist is eligible to remove album

            //  Verify album name
            //  First we find the user's playlists
            UserPlaylists allPlaylists = null;

            for (UserPlaylists userPlaylists : usersPlaylists) {
                if (userPlaylists.getUser().equals(artist)) {
                    allPlaylists = userPlaylists;
                    break;
                }
            }

            boolean hasAlbum = false;
            Album crtAlbum = null;

            //  Now we check the name
            for (Album album : allPlaylists.getAlbums()) {
                if (album.getName().equals(crtCommand.getName())) {
                    hasAlbum = true;
                    crtAlbum = album;
                    break;
                }
            }

            if (!hasAlbum) {
                message = crtCommand.getUsername()
                        + " doesn't have an album with the given name.";
            } else {
                //  Last check

                ArrayList<SongSelection> loadedArtistSongs = new ArrayList<>();
                boolean used = false;

                //  We need to see if the album is loaded
                for (ItemSelection item : player) {
                    if (item instanceof SongSelection) {
                        if (((SongSelection) item).getSong().getArtist()
                                .equals(crtCommand.getUsername())) {
                            loadedArtistSongs.add(((SongSelection) item));
                        }
                    }
                    if (item instanceof AlbumSelection) {
                        if (((AlbumSelection) item).getAlbum()
                                .equals(crtAlbum)) {
                            used = true;
                            break;
                        }
                    }
                }

                //  We need to see if any song is loaded
                for (SongSelection song : loadedArtistSongs) {
                    if (crtAlbum.getSongs().contains(song.getSong())) {
                        used = true;
                        break;
                    }
                }

                //  We need to check if a playlist has any of the album songs
                for (SongInput song : crtAlbum.getSongs()) {
                    for (Playlist playlist : playlists) {
                        if (playlist.getSongs().contains(song)) {
                            used = true;
                            break;
                        }
                    }
                }

                if (used) {
                    message = crtCommand.getUsername()
                            + " can't delete this album.";
                } else {
                    //  Album can be safely deleted

                    //  Remove songs
                    for (SongInput song : crtAlbum.getSongs()) {
                        for (SongLikes songLikes : songsLikes) {
                            if (songLikes.getSong().equals(song)) {
                                //  If the song has at least a like
                                //  It must be removed from users' liked songs
                                for (UserPlaylists userPlaylists : usersPlaylists) {
                                    userPlaylists.getLikedSongs().remove(song);

                                    //  From the artist's playlist also remove the album
                                    if (userPlaylists.getUser().getUsername()
                                            .equals(crtCommand.getUsername())) {
                                        userPlaylists.getAlbums().remove(crtAlbum);
                                    }
                                }
                                songsLikes.remove(songLikes);
                                break;
                            }
                        }
                        library.getSongs().remove(song);
                    }

                    //  Remove album from database
                    albums.remove(crtAlbum);

                    message = crtCommand.getUsername()
                            + "  deleted the album successfully.";
                }
            }
        }

        return message;
    }

    /**
     * This method changes the current page
     *
     * @param crtCommand The addUser command with all its data
     * @param usersPlaylists The array of users and their respective playlists
     * @param pageSystem Array of all the pages in the system
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getChangePageMessage(final Command crtCommand,
                                              final ArrayList<UserPlaylists> usersPlaylists,
                                              final ArrayList<Page> pageSystem) {
        String message;

        //  Find the user's page
        Page crtPage = null;

        for (Page page : pageSystem) {
            if (page.getPageOwner().getUsername().equals(crtCommand.getUsername())) {
                crtPage = page;
                break;
            }
        }

        switch (crtCommand.getNextPage()) {
            case "Home" -> {
                //  Finding the user's original playlists
                for (UserPlaylists userPlaylists : usersPlaylists) {
                    if (userPlaylists.getUser().equals(crtPage.getPageOwner())) {
                        crtPage.setUserPlaylists(userPlaylists);
                        break;
                    }
                }

                //  Updating current page
                crtPage.setCurrentPage("Home");

                //  Removing any previous connections
                crtPage.setHostInfo(null);
                crtPage.setManagement(null);

                message = crtCommand.getUsername()
                        + " accessed Home successfully.";
            }

            case "LikedContent" -> {
                //  Finding the user's original playlists
                for (UserPlaylists userPlaylists : usersPlaylists) {
                    if (userPlaylists.getUser().equals(crtPage.getPageOwner())) {
                        crtPage.setUserPlaylists(userPlaylists);
                        break;
                    }
                }

                //  Updating current page
                crtPage.setCurrentPage("LikedContent");

                //  Removing any previous connections
                crtPage.setHostInfo(null);
                crtPage.setManagement(null);

                message = crtCommand.getUsername()
                        + " accessed LikedContent successfully.";
            }

            default -> {
                message = crtCommand.getUsername()
                        + "  is trying to access a non-existent page.";
            }
        }

        return message;
    }

    /**
     * This method removes a podcast
     *
     * @param crtCommand The addUser command with all its data
     * @param usersPlaylists The array of users and their respective playlists
     * @param player The array that keeps all user players in check
     * @param library Singleton containing all songs, users and podcasts
     * @param podcasts The array that keeps track of all the podcasts
     *                 when they are not loaded
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getRemovePodcastMessage(final Command crtCommand,
                                                 final ArrayList<UserPlaylists> usersPlaylists,
                                                 final ArrayList<ItemSelection> player,
                                                 final LibraryInput library,
                                                 final ArrayList<PodcastSelection> podcasts) {
        String message;

        UserInput host = null;
        boolean exists = false;
        boolean isHost = false;

        //  Checking to see host availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("host")) {
                    isHost = true;
                    host = user;
                }
                break;
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isHost) {
            message = crtCommand.getUsername() + " is not a host.";
        } else {
            //  Host is eligible to remove podcast

            //  Verify podcast name
            //  First we find the user's playlists
            UserPlaylists allPlaylists = null;

            for (UserPlaylists userPlaylists : usersPlaylists) {
                if (userPlaylists.getUser().equals(host)) {
                    allPlaylists = userPlaylists;
                    break;
                }
            }

            boolean hasPodcast = false;
            PodcastInput crtPodcast = null;

            //  Now we check the name
            for (PodcastInput podcast : allPlaylists.getPodcasts()) {
                if (podcast.getName().equals(crtCommand.getName())) {
                    hasPodcast = true;
                    crtPodcast = podcast;
                    break;
                }
            }

            if (!hasPodcast) {
                message = crtCommand.getUsername()
                        + " doesn't have a podcast with the given name.";
            } else {
                //  Last check

                boolean used = false;

                //  We need to see if the podcast is loaded
                for (ItemSelection item : player) {
                    if (item instanceof PodcastSelection) {
                        if (((PodcastSelection) item).getPodcast()
                                .equals(crtPodcast)) {
                            used = true;
                            break;
                        }
                    }
                }

                if (used) {
                    message = crtCommand.getUsername()
                            + " can't delete this podcast.";
                } else {
                    //  Podcast can be safely deleted

                    //  Remove podcast from paused podcasts
                    for (PodcastSelection podcast : podcasts) {
                        if (podcast.getPodcast().equals(crtPodcast)) {
                            podcasts.remove(podcast);
                            break;
                        }
                    }

                    //  Remove podcast from host's podcasts
                    allPlaylists.getPodcasts().remove(crtPodcast);

                    //  Remove podcast from database
                    library.getPodcasts().remove(crtPodcast);

                    message = crtCommand.getUsername()
                            + " deleted the podcast successfully.";
                }
            }
        }

        return message;
    }

    /**
     * This method removes an event
     *
     * @param crtCommand The addUser command with all its data
     * @param library Singleton containing all songs, users and podcasts
     * @param managements The array of managing technicalities for artists
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getRemoveEventMessage(final Command crtCommand,
                                               final LibraryInput library,
                                               final ArrayList<Management> managements) {
        String message;

        UserInput artist = null;
        boolean exists = false;
        boolean isArtist = false;

        //  Checking to see artist availability
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                exists = true;
                if (user.getType().equals("artist")) {
                    isArtist = true;
                    artist = user;
                }
            }
        }

        if (!exists) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!isArtist) {
            message = crtCommand.getUsername() + " is not an artist.";
        } else {
            //  Artist may remove event

            //  We need to check if the event exists
            boolean hasEvent = false;
            Event crtEvent = null;
            ArrayList<Event> allEvents = null;

            for (Management management : managements) {
                if (management.getArtist().equals(artist)) {
                    allEvents = management.getEvents();
                    break;
                }
            }

            //  Browsing through events
            for (Event event : allEvents) {
                if (event.getName().equals(crtCommand.getName())) {
                    hasEvent = true;
                    crtEvent = event;
                    break;
                }
            }

            if (!hasEvent) {
                message = crtCommand.getUsername()
                        + " doesn't have an event with the given name.";
            } else {
                //  Artist can safely delete this event
                allEvents.remove(crtEvent);

                message = crtCommand.getUsername()
                        + " deleted the event successfully.";
            }
        }

        return message;
    }
}
