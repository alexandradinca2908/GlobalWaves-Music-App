package main.UtilityClasses;

import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import main.ArtistClasses.Management;
import main.CommandHelper.Command;
import main.PagingClasses.Page;
import main.PlaylistClasses.Album;
import main.PlaylistClasses.Playlist;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.SongClasses.SongLikes;
import main.PlaylistClasses.UserPlaylists;
import main.VisitorPattern.VisitorClasses.VisitNextMessage;
import main.VisitorPattern.VisitorClasses.VisitPrevMessage;
import main.VisitorPattern.Visitor;

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
        } else if (lastSearchResult.get(0).equals("artist")){
            int index = crtCommand.getItemNumber();
            message = "Successfully selected " + lastSearchResult.get(index) + "'s page.";
        } else {
            //  Selecting something to play
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
            Visitor visitNextMessage = new VisitNextMessage(player,
                    podcasts, crtCommand);

            message = crtItem.accept(visitNextMessage);
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
            Visitor visitPrevMessage = new VisitPrevMessage(crtCommand);

            message = crtItem.accept(visitPrevMessage);
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
            updatePlayer(player, crtCommand, podcasts, library);
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
                                           final ArrayList<Management> managements) {
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

            //  Management, if the user is an artist
            if (newUser.getType().equals("artist")) {
                Management newManagement = new Management();
                newManagement.setArtist(newUser);
                managements.add(newManagement);
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
                    for (SongInput song : newAlbum.getSongs()) {
                        library.getSongs().add(song);
                    }

                    message = crtCommand.getUsername()
                            + " has added new album successfully.";
                }
            }
        }

        return message;
    }
}
