package main.UtilityClasses;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import main.CommandHelper.Command;
import main.PlaylistClasses.Playlist;
import main.Selections.ItemSelection;
import main.Selections.PlaylistSelection;
import main.Selections.PodcastSelection;
import main.Selections.SongSelection;
import main.SongClasses.SongLikes;
import main.PlaylistClasses.UserPlaylists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
            //  Keep the original order for unshuffle

            copyItem.getPlaylist().setOriginalSongOrder(new ArrayList<>(
                    copyItem.getPlaylist().getSongs()));

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
            Collections.shuffle(copyItem.getPlaylist().getSongs(),
                    new Random(crtCommand.getSeed()));

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
            message = "Shuffle function activated successfully.";

            //  Set shuffle status
            copyItem.setShuffle(true);

            //  Unshuffle
        } else {
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

            //  Reset the order
            copyItem.getPlaylist().getSongs().clear();
            copyItem.getPlaylist().getSongs().addAll(copyItem.getPlaylist().getOriginalSongOrder());
            copyItem.getPlaylist().getOriginalSongOrder().clear();
            copyItem.getPlaylist().setOriginalSongOrder(null);

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
     * This method selects one item from the last search
     *
     * @param lastSearchResult The array containing the search result and its type
     * @param crtCommand The select command with all its data
     * @param steps The array that checks whether search and select were executed
     * @return Based on the operation, it returns an appropriate message
     */
    public static String getSelectMessage(final ArrayList<String> lastSearchResult,
                                          final Command crtCommand, final int[] steps) {
        String message;
        if (steps[0] == 0) {
            message = "Please conduct a search before making a selection.";
        } else if (crtCommand.getItemNumber() > lastSearchResult.size() - 1) {
            message = "The selected ID is too high.";
        } else {
            int index = crtCommand.getItemNumber();
            message = "Successfully selected " + lastSearchResult.get(index) + ".";
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
        SongInput crtSong = null;
        Playlist copyPlaylist = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                loaded = 1;
                if (item instanceof SongSelection) {
                    isSong = 1;
                    crtSong = ((SongSelection) item).getSong();
                }
                break;
            }
        }

        if (loaded == 0) {
            message = "Please load a source before adding to or removing from the playlist.";

        } else if (isSong == 0) {
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
        SongInput crtSong = null;
        PlaylistSelection crtPlaylist = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                loaded = 1;
                if (item instanceof SongSelection) {
                    isSong = 1;
                    crtSong = ((SongSelection) item).getSong();
                } else if (item instanceof PlaylistSelection) {
                    isPlaylist = 1;
                    crtPlaylist = (PlaylistSelection) item;
                }
                break;
            }
        }

        if (loaded == 0) {
            message = "Please load a source before liking or unliking.";

        } else if (isSong == 0 && isPlaylist == 0) {
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
        } else {
            //  We need to calculate which song we are currently at and store it
            SongInput crtSongInPlaylist = null;
            int duration = crtPlaylist.getPlaylist().getDuration();

            //  Calculating based on current time
            crtPlaylist.updateRemainingTime(crtCommand.getTimestamp());

            for (SongInput song : crtPlaylist.getPlaylist().getSongs()) {
                duration -= song.getDuration();

                if (duration <= crtPlaylist.getRemainingTime()) {
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
            if (crtItem instanceof SongSelection) {
                message = "Please load a source before skipping to the next track.";

                crtItem.setRemainingTime(0);
                crtItem.setPaused(true);

                player.remove(crtItem);

            } else if (crtItem instanceof PodcastSelection) {
                PodcastSelection copyItem = (PodcastSelection) crtItem;
                EpisodeInput crtEp = null;

                //  Find the current episode
                int duration = copyItem.getPodcast().getDuration();

                for (EpisodeInput episode : copyItem.getPodcast().getEpisodes()) {
                    duration -= episode.getDuration();

                    if (duration < copyItem.getRemainingTime()) {
                        crtEp = episode;
                        break;
                    }
                }

                if (duration > 0) {
                    copyItem.setRemainingTime(duration);
                    copyItem.setPaused(false);
                    int index = copyItem.getPodcast().getEpisodes().indexOf(crtEp);

                    message = "Skipped to next track successfully. The current track is "
                            + copyItem.getPodcast().getEpisodes().get(index + 1).getName() + ".";

                } else if (duration == 0) {
                    //  Check repeat status to take action
                    if (copyItem.getRepeat().equals("No Repeat")) {
                        //  Stop podcast
                        copyItem.setRemainingTime(0);
                        copyItem.setPaused(true);
                        player.remove(copyItem);
                        podcasts.remove(copyItem);

                        message = "Please load a source before skipping to the next track.";

                    } else if (copyItem.getRepeat().equals("Repeat Once")) {
                        //  Restart podcast
                        copyItem.setRemainingTime(copyItem.getPodcast().getDuration());
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);
                        copyItem.setRepeat("No Repeat");

                        message = "Skipped to next track successfully. The current track is "
                                + copyItem.getPodcast().getEpisodes().get(0).getName() + ".";

                    } else if (copyItem.getRepeat().equals("Repeat Infinite")) {
                        //  Restart podcast but keep repeat status
                        copyItem.setRemainingTime(copyItem.getPodcast().getDuration());
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        message = "Skipped to next track successfully. The current track is "
                                + copyItem.getPodcast().getEpisodes().get(0).getName() + ".";
                    }
                }
            } else if (crtItem instanceof PlaylistSelection) {
                PlaylistSelection copyItem = (PlaylistSelection) crtItem;
                SongInput crtSong = null;

                //  Find the current song
                int duration = copyItem.getPlaylist().getDuration();
                int prevDuration = duration;

                for (SongInput song : copyItem.getPlaylist().getSongs()) {
                    prevDuration = duration;
                    duration -= song.getDuration();

                    if (duration < copyItem.getRemainingTime()) {
                        crtSong = song;
                        break;
                    }
                }

                if (duration > 0) {
                    if (copyItem.getRepeat().equals("Repeat Current Song")) {
                        //  Restart song
                        copyItem.setRemainingTime(prevDuration);
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        message = "Skipped to next track successfully. The current track is "
                                + crtSong.getName() + ".";
                    } else {
                        //  Next song
                        copyItem.setRemainingTime(duration);
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        int index = copyItem.getPlaylist().getSongs().indexOf(crtSong);

                        message = "Skipped to next track successfully. The current track is "
                                + copyItem.getPlaylist().getSongs().get(index + 1).getName() + ".";
                    }
                } else if (duration == 0) {
                    //  Check repeat status to take action
                    if (copyItem.getRepeat().equals("No Repeat")) {
                        //  Stop playlist
                        copyItem.setRemainingTime(0);
                        copyItem.setPaused(true);
                        player.remove(copyItem);

                        message = "Please load a source before skipping to the next track.";

                    } else if (copyItem.getRepeat().equals("Repeat All")) {
                        //  Restart playlist
                        copyItem.setRemainingTime(copyItem.getPlaylist().getDuration());
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        message = "Skipped to next track successfully. The current track is "
                                + copyItem.getPlaylist().getSongs().get(0).getName() + ".";

                    } else if (copyItem.getRepeat().equals("Repeat Current Song")) {
                        //  Restart song
                        copyItem.setRemainingTime(prevDuration);
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        message = "Skipped to next track successfully. The current track is "
                                + crtSong.getName() + ".";
                    }
                }
            }
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
            if (crtItem instanceof SongSelection) {
                crtItem.setRemainingTime(((SongSelection) crtItem).getSong().getDuration());
                crtItem.setPaused(false);

                message = "Returned to previous track successfully. The current track is "
                        + ((SongSelection) crtItem).getSong().getName() + ".";

            } else if (crtItem instanceof PodcastSelection) {
                PodcastSelection copyItem = (PodcastSelection) crtItem;
                EpisodeInput crtEp = null;

                //  Find the current episode
                int duration = copyItem.getPodcast().getDuration();

                for (EpisodeInput episode : copyItem.getPodcast().getEpisodes()) {
                    duration -= episode.getDuration();

                    if (duration < copyItem.getRemainingTime()) {
                        duration += episode.getDuration();
                        crtEp = episode;
                        break;
                    }
                }

                if (duration - crtItem.getRemainingTime() > 1) {
                    copyItem.setRemainingTime(duration);
                    copyItem.setStartTime(crtCommand.getTimestamp());
                    copyItem.setPaused(false);

                    message = "Returned to previous track successfully. The current track is "
                            + crtEp.getName() + ".";

                } else {
                    //  Treating first episode exception
                    if (copyItem.getPodcast().getEpisodes().indexOf(crtEp) == 0) {
                        //  If we are at the first episode, restart the podcast
                        copyItem.setRemainingTime(copyItem.getPodcast().getDuration());
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        message = "Returned to previous track successfully. The current track is "
                                + copyItem.getPodcast().getEpisodes().get(0).getName() + ".";

                        //  Lastly we can go back to the previous episode
                    } else {
                        int index = copyItem.getPodcast().getEpisodes().indexOf(crtEp) - 1;
                        copyItem.setRemainingTime(duration
                                + copyItem.getPodcast().getEpisodes().get(index).getDuration());
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        message = "Returned to previous track successfully. The current track is "
                                + copyItem.getPodcast().getEpisodes().get(0).getName() + ".";
                    }
                }

            } else if (crtItem instanceof PlaylistSelection) {
                PlaylistSelection copyItem = (PlaylistSelection) crtItem;
                SongInput crtSong = null;

                //  Find the current episode
                int duration = copyItem.getPlaylist().getDuration();

                for (SongInput song : copyItem.getPlaylist().getSongs()) {
                    duration -= song.getDuration();

                    if (duration < copyItem.getRemainingTime()) {
                        duration += song.getDuration();
                        crtSong = song;
                        break;
                    }
                }

                if (duration - crtItem.getRemainingTime() > 1) {
                    copyItem.setRemainingTime(duration);
                    copyItem.setStartTime(crtCommand.getTimestamp());
                    copyItem.setPaused(false);

                    message = "Returned to previous track successfully. The current track is "
                            + crtSong.getName() + ".";

                } else {
                    //  Treating first song exception
                    if (copyItem.getPlaylist().getSongs().indexOf(crtSong) == 0) {
                        //  If we are at the first song, just restart the playlist
                        copyItem.setRemainingTime(copyItem.getPlaylist().getDuration());
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        message = "Returned to previous track successfully. "
                                + "The current track is "
                                + copyItem.getPlaylist().getSongs().get(0).getName() + ".";

                        //  Now we can go back to the previous song
                    } else {
                        int index = copyItem.getPlaylist().getSongs().indexOf(crtSong) - 1;
                        copyItem.setRemainingTime(duration
                                + copyItem.getPlaylist().getSongs().get(index).getDuration());
                        copyItem.setStartTime(crtCommand.getTimestamp());
                        copyItem.setPaused(false);

                        message = "Returned to previous track successfully. The current track is "
                                + copyItem.getPlaylist().getSongs().get(index).getName() + ".";
                    }
                }
            }
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
}