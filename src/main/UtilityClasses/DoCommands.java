package main.UtilityClasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import main.CommandHelper.Command;
import main.CommandHelper.Filters;
import main.PlaylistClasses.Album;
import main.PlaylistClasses.Playlist;
import main.PlaylistClasses.UserPlaylists;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.PlaylistSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.SongClasses.SongLikes;

import java.util.ArrayList;
import java.util.Collections;

import static main.Main.updatePlayer;
import static main.UtilityClasses.GetMessages.getSwitchConnectionMessage;

public final class DoCommands {

    private DoCommands() {
    }

    /**
     * Main method call for search command
     *
     * @param player The array that keeps all user players in check
     * @param crtCommand Current command
     * @param podcasts The array that keeps track of all the podcasts
     *                 when they are not loaded
     * @param objectMapper Object Mapper
     * @param library Singleton containing all songs, users and podcasts
     * @param lastSearchResult The array containing the search result and its type
     * @param steps The array that checks whether search and select were executed
     * @param playlists The array of all user playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSearch(final ArrayList<ItemSelection> player,
                                      final Command crtCommand,
                                      final ArrayList<PodcastSelection> podcasts,
                                      final ObjectMapper objectMapper,
                                      final LibraryInput library,
                                      final ArrayList<String> lastSearchResult,
                                      final int[] steps,
                                      final ArrayList<Playlist> playlists) {
        //  Searching for a song
        ObjectNode searchOutput = objectMapper.createObjectNode();

        //  Setting the output
        searchOutput.put("command", "search");
        searchOutput.put("user", crtCommand.getUsername());
        searchOutput.put("timestamp", crtCommand.getTimestamp());

        //  Update all players first
        updatePlayer(player, crtCommand, podcasts, library);

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    searchOutput.put("message", user.getUsername() + " is offline.");
                    searchOutput.putPOJO("results", new ArrayList<>());

                    return searchOutput;
                }
            }
        }

        //  Clear user's player
        if (!player.isEmpty()) {
            for (ItemSelection item : player) {
                if (item.getUser().equals(crtCommand.getUsername())) {
                    if (item instanceof PodcastSelection) {
                        item.updateRemainingTime(crtCommand.getTimestamp());
                        item.setPaused(true);
                    }
                    player.remove(item);
                    break;
                }
            }
        }

        switch (crtCommand.getType()) {
            case "song" -> {
                Filters filters = crtCommand.getFilters();
                ArrayList<SongInput> result = new ArrayList<>();

                //  Found songs will be added in result array
                SearchSelect.searchForSongs(filters, result, library);

                //  Truncate results if needed
                if (result.size() > Constants.MAX_SIZE_5) {
                    result.subList(Constants.MAX_SIZE_5, result.size()).clear();
                }

                //  Setting the message
                searchOutput.put("message", "Search returned "
                        + result.size() + " results");

                //  Extracting the names of the songs
                ArrayList<String> songNames = new ArrayList<>();
                for (SongInput songInput : result) {
                    songNames.add(songInput.getName());
                }
                searchOutput.putPOJO("results", songNames);

                //  Storing the result in case we need to select it later
                SearchSelect.storeResultForSelect(lastSearchResult, songNames, "song");
                steps[0] = 1;
            }

            case "playlist" -> {
                Filters filters = crtCommand.getFilters();
                ArrayList<Playlist> result = new ArrayList<>();

                //  Found playlists will be added in result array
                SearchSelect.searchForPlaylists(filters, result, playlists);

                //  Taking out private playlists
                String username = crtCommand.getUsername();
                result.removeIf(playlist -> !playlist.getOwner().equals(username)
                        && !playlist.isVisibility());

                //  Truncate results if needed
                if (result.size() > Constants.MAX_SIZE_5) {
                    result.subList(Constants.MAX_SIZE_5, result.size()).clear();
                }

                //  Setting the message
                searchOutput.put("message", "Search returned "
                        + result.size() + " results");

                //  Extracting the names of the playlists
                ArrayList<String> playlistNames = new ArrayList<>();
                for (Playlist playlist : result) {
                    playlistNames.add(playlist.getName());
                }
                searchOutput.putPOJO("results", playlistNames);

                //  Storing the result in case we need to select it later
                SearchSelect.storeResultForSelect(lastSearchResult,
                        playlistNames, "playlist");
                steps[0] = 1;
            }

            case "podcast" -> {
                Filters filters = crtCommand.getFilters();
                ArrayList<PodcastInput> result = new ArrayList<>();

                //  Found podcasts will be added in result array
                SearchSelect.searchForPodcasts(filters, result, library);

                //  Truncate results if needed
                if (result.size() > Constants.MAX_SIZE_5) {
                    result.subList(Constants.MAX_SIZE_5, result.size()).clear();
                }

                //  Setting the message
                searchOutput.put("message", "Search returned "
                        + result.size() + " results");

                //  Extracting the names of the podcasts
                ArrayList<String> podcastNames = new ArrayList<>();
                for (PodcastInput podcastInput : result) {
                    podcastNames.add(podcastInput.getName());
                }
                searchOutput.putPOJO("results", podcastNames);

                //  Storing the result in case we need to select it later
                SearchSelect.storeResultForSelect(lastSearchResult,
                        podcastNames, "podcast");
                steps[0] = 1;
            }

            default -> { }
        }

        return searchOutput;
    }

    /**
     * Main method call for select command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param lastSearchResult The array containing the search result and its type
     * @param steps The array that checks whether search and select were executed
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSelect(final ObjectMapper objectMapper,
                                      final Command crtCommand,
                                      final ArrayList<String> lastSearchResult,
                                      final int[] steps,
                                      final LibraryInput library) {
        //  Setting the output
        ObjectNode selectOutput = objectMapper.createObjectNode();
        selectOutput.put("command", "select");
        selectOutput.put("user", crtCommand.getUsername());
        selectOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    selectOutput.put("message", user.getUsername() + " is offline.");
                    selectOutput.putPOJO("results", new ArrayList<>());

                    return selectOutput;
                }
            }
        }

        //  Creating the message
        String message = GetMessages.getSelectMessage(lastSearchResult,
                crtCommand, steps);
        selectOutput.put("message", message);

        //  Storing the selection in case we need to load it
        if (message.contains("Successfully selected")) {
            int index = crtCommand.getItemNumber();
            String result = lastSearchResult.get(index);

            //  Keeping only the required value in the array (and its type)
            lastSearchResult.add(1, result);
            lastSearchResult.subList(2, lastSearchResult.size()).clear();

            //  Last result is initialized properly for loading
            steps[1] = 1;
        }

        return selectOutput;
    }

    /**
     * Main method call for load command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param steps The array that checks whether search and select were executed
     * @param lastSearchResult The array containing the search result and its type
     * @param library Singleton containing all songs, users and podcasts
     * @param player The array that keeps all user players in check
     * @param playlists The array of all user playlists
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doLoad(final ObjectMapper objectMapper,
                                    final Command crtCommand,
                                    final int[] steps,
                                    final ArrayList<String> lastSearchResult,
                                    final LibraryInput library,
                                    final ArrayList<ItemSelection> player,
                                    final ArrayList<Playlist> playlists,
                                    final ArrayList<PodcastSelection> podcasts) {
        ObjectNode loadOutput = objectMapper.createObjectNode();
        loadOutput.put("command", "load");
        loadOutput.put("user", crtCommand.getUsername());
        loadOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    loadOutput.put("message", user.getUsername() + " is offline.");
                    loadOutput.putPOJO("results", new ArrayList<>());

                    return loadOutput;
                }
            }
        }

        //  Adding the appropriate load message
        if (steps[1] == 1 && lastSearchResult.isEmpty()) {
            loadOutput.put("message", "You can't load an empty audio collection!");
        } else if (steps[1] == 0) {
            loadOutput.put("message", "Please select a source"
                    + " before attempting to load.");
        } else {
            loadOutput.put("message", "Playback loaded successfully.");

            //  Loading the song into the database
            if (lastSearchResult.get(0).equals("song")) {
                SongSelection selectedSong = SearchSelect.getSongSelection(crtCommand,
                        library, lastSearchResult);

                //  Clearing other load from the same user
                for (ItemSelection item : player) {
                    if (item.getUser().equals(selectedSong.getUser())) {
                        player.remove(item);
                        break;
                    }
                }

                //  Add selection to array
                player.add(selectedSong);
            }

            //  Loading the playlist into the database
            if (lastSearchResult.get(0).equals("playlist")) {
                PlaylistSelection selectedPlaylist =
                        SearchSelect.getPlaylistSelection(crtCommand,
                                playlists, lastSearchResult);

                //  Clearing other load from the same user
                for (ItemSelection item : player) {
                    if (item.getUser().equals(selectedPlaylist.getUser())) {
                        player.remove(item);
                        break;
                    }
                }

                //  Add selection to array
                player.add(selectedPlaylist);
            }

            //  Loading the podcast into the database
            if (lastSearchResult.get(0).equals("podcast")) {
                PodcastSelection selectedPodcast =
                        SearchSelect.getPodcastSelection(crtCommand,
                                library, lastSearchResult);

                //  Clearing other load from the same user
                for (ItemSelection item : player) {
                    if (item.getUser().equals(selectedPodcast.getUser())) {
                        player.remove(item);
                        break;
                    }
                }

                //  Check to see if the podcast has been started by this user already
                int started = 0;
                for (PodcastSelection podcast : podcasts) {
                    if (podcast.getUser().equals(selectedPodcast.getUser())) {
                        if (podcast.getPodcast().equals(selectedPodcast.getPodcast())) {
                            //  Resume podcast and update info
                            player.add(podcast);
                            podcast.setPaused(false);
                            podcast.setStartTime(crtCommand.getTimestamp());
                            started = 1;

                            break;
                        }
                    }
                }

                if (started == 0) {
                    //  Add selection to array
                    player.add(selectedPodcast);

                    //  Keep record of the selection
                    podcasts.add(selectedPodcast);
                }
            }

            //  Clearing the result so that we can't load it twice
            lastSearchResult.clear();
            //  Reset steps
            steps[0] = 0;
            steps[1] = 0;
        }

        return loadOutput;
    }

    /**
     * Main method call for status command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param player The array that keeps all user players in check
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doStatus(final ObjectMapper objectMapper,
                                      final Command crtCommand,
                                      final ArrayList<ItemSelection> player,
                                      final ArrayList<PodcastSelection> podcasts,
                                      final LibraryInput library) {
        ObjectNode statusOutput = objectMapper.createObjectNode();
        statusOutput.put("command", "status");
        statusOutput.put("user", crtCommand.getUsername());
        statusOutput.put("timestamp", crtCommand.getTimestamp());

        //  Update the player
        updatePlayer(player, crtCommand, podcasts, library);

        String user = crtCommand.getUsername();
        ItemSelection reqItem = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(user)) {
                reqItem = item;
            }
        }

        //  Setting the stats
        ObjectNode stats = ItemSelection.getStats(reqItem, objectMapper);
        statusOutput.set("stats", stats);

        return statusOutput;
    }

    /**
     * Main method call for playPause command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param player The array that keeps all user players in check
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doPlayPause(final ObjectMapper objectMapper,
                                         final Command crtCommand,
                                         final ArrayList<ItemSelection> player,
                                         final ArrayList<PodcastSelection> podcasts,
                                         final LibraryInput library) {
        ObjectNode playPauseOutput = objectMapper.createObjectNode();

        playPauseOutput.put("command", "playPause");
        playPauseOutput.put("user", crtCommand.getUsername());
        playPauseOutput.put("timestamp", crtCommand.getTimestamp());

        //  Update the player
        updatePlayer(player, crtCommand, podcasts, library);

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    playPauseOutput.put("message", user.getUsername() + " is offline.");
                    playPauseOutput.putPOJO("results", new ArrayList<>());

                    return playPauseOutput;
                }
            }
        }

        //  Looking for what the user is playing
        int found = 0;
        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                if (item.isPaused()) {
                    playPauseOutput.put("message", "Playback resumed successfully.");

                    //  Resume
                    item.setPaused(false);

                    //  Set start time
                    item.setStartTime(crtCommand.getTimestamp());

                } else {
                    playPauseOutput.put("message", "Playback paused successfully.");

                    //  Pause
                    item.setPaused(true);
                }
                found = 1;
                break;
            }
        }

        if (found == 0) {
            String message = "Please load a source before "
                    + "attempting to pause or resume playback.";
            playPauseOutput.put("message", message);
        }

        return playPauseOutput;
    }

    /**
     * Main method call for doCreatePlaylist command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param playlists The array of all user playlists
     * @param usersPlaylists The array of users and their respective playlists
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doCreatePlaylist(final ObjectMapper objectMapper,
                                              final Command crtCommand,
                                              final ArrayList<Playlist> playlists,
                                              final ArrayList<UserPlaylists> usersPlaylists,
                                              final LibraryInput library) {
        ObjectNode createPlaylistOutput = objectMapper.createObjectNode();

        createPlaylistOutput.put("command", "createPlaylist");
        createPlaylistOutput.put("user", crtCommand.getUsername());
        createPlaylistOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    createPlaylistOutput.put("message", user.getUsername() + " is offline.");
                    createPlaylistOutput.putPOJO("results", new ArrayList<>());

                    return createPlaylistOutput;
                }
            }
        }

        int exists = 0;
        for (Playlist playlist : playlists) {
            if (playlist.getOwner().equals(crtCommand.getUsername())) {
                if (playlist.getName().equals(crtCommand.getPlaylistName())) {
                    String message = "A playlist with the same name already exists.";
                    createPlaylistOutput.put("message", message);
                    exists = 1;

                    break;
                }
            }
        }

        if (exists == 0) {
            String playlistName = crtCommand.getPlaylistName();
            String username = crtCommand.getUsername();
            Playlist newPlaylist = new Playlist(playlistName, username);
            //  Add playlist in general list
            playlists.add(newPlaylist);
            //  Add playlist in user's list
            for (UserPlaylists user : usersPlaylists) {
                if (user.getUser().getUsername().equals(crtCommand.getUsername())) {
                    user.getPlaylists().add(newPlaylist);
                    break;
                }
            }

            createPlaylistOutput.put("message", "Playlist created successfully.");
        }

        return createPlaylistOutput;
    }

    /**
     * Main method call for doAddRemoveInPlaylist command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param player The array that keeps all user players in check
     * @param playlists The array of all user playlists
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doAddRemoveInPlaylist(final ObjectMapper objectMapper,
                                                   final Command crtCommand,
                                                   final ArrayList<ItemSelection> player,
                                                   final ArrayList<Playlist> playlists,
                                                   final LibraryInput library) {
        ObjectNode addRemoveOutput = objectMapper.createObjectNode();

        addRemoveOutput.put("command", "addRemoveInPlaylist");
        addRemoveOutput.put("user", crtCommand.getUsername());
        addRemoveOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    addRemoveOutput.put("message", user.getUsername() + " is offline.");
                    addRemoveOutput.putPOJO("results", new ArrayList<>());

                    return addRemoveOutput;
                }
            }
        }

        //  Get message and make proper modifications to the playlist
        String message = GetMessages.getAddRemoveMessage(player, playlists, crtCommand);
        addRemoveOutput.put("message", message);

        return addRemoveOutput;
    }

    /**
     * Main method call for doLike command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param player The array that keeps all user players in check
     * @param usersPlaylists The array of users and their respective playlists
     * @param songsLikes The array of songs and their respective likes
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doLike(final ObjectMapper objectMapper,
                                    final Command crtCommand,
                                    final ArrayList<ItemSelection> player,
                                    final ArrayList<UserPlaylists> usersPlaylists,
                                    final ArrayList<SongLikes> songsLikes,
                                    final LibraryInput library) {
        ObjectNode likeOutput = objectMapper.createObjectNode();

        likeOutput.put("command", "like");
        likeOutput.put("user", crtCommand.getUsername());
        likeOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    likeOutput.put("message", user.getUsername() + " is offline.");

                    return likeOutput;
                }
            }
        }

        //  Get message and make proper modifications to the user's liked songs
        String message = GetMessages.getLikeMessage(player, usersPlaylists,
                crtCommand, songsLikes);

        likeOutput.put("message", message);

        return likeOutput;
    }

    /**
     * Main method call for doShowPlaylist command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param usersPlaylists The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doShowPlaylists(final ObjectMapper objectMapper,
                                             final Command crtCommand,
                                             final ArrayList<UserPlaylists> usersPlaylists) {
        ObjectNode showPlaylistsOutput = objectMapper.createObjectNode();

        showPlaylistsOutput.put("command", "showPlaylists");
        showPlaylistsOutput.put("user", crtCommand.getUsername());
        showPlaylistsOutput.put("timestamp", crtCommand.getTimestamp());

        ArrayList<ObjectNode> result = new ArrayList<>();

        UserPlaylists user = null;

        for (UserPlaylists userP : usersPlaylists) {
            if (userP.getUser().getUsername().equals(crtCommand.getUsername())) {
                user = userP;
                break;
            }
        }

        if (user != null) {
            for (Playlist playlist : user.getPlaylists()) {
                ObjectNode resultNode = objectMapper.createObjectNode();

                //  Set playlist data
                resultNode.put("name", playlist.getName());

                ArrayList<String> songNames = new ArrayList<>();

                ArrayList<SongInput> correctSongOrder;

                if (playlist.getOriginalSongOrder() != null) {
                    correctSongOrder = playlist.getOriginalSongOrder();
                } else {
                    correctSongOrder = playlist.getSongs();
                }

                for (SongInput song : correctSongOrder) {
                    songNames.add(song.getName());
                }
                resultNode.putPOJO("songs", songNames);

                if (playlist.isVisibility()) {
                    resultNode.put("visibility", "public");
                } else {
                    resultNode.put("visibility", "private");
                }

                resultNode.put("followers", playlist.getFollowers().size());

                result.add(resultNode);
            }

            showPlaylistsOutput.putPOJO("result", result);

        } else {
            showPlaylistsOutput.put("result", "");
        }

        return showPlaylistsOutput;
    }

    /**
     * Main method call for doShowPreferredSongs command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param usersPlaylists The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doShowPreferredSongs(final ObjectMapper objectMapper,
                                                  final Command crtCommand,
                                                  final ArrayList<UserPlaylists> usersPlaylists) {
        ObjectNode showPreferredSongsOutput = objectMapper.createObjectNode();

        showPreferredSongsOutput.put("command", "showPreferredSongs");
        showPreferredSongsOutput.put("user", crtCommand.getUsername());
        showPreferredSongsOutput.put("timestamp", crtCommand.getTimestamp());

        ArrayList<String> songNames = new ArrayList<>();

        for (UserPlaylists user : usersPlaylists) {
            if (user.getUser().getUsername().equals(crtCommand.getUsername())) {
                for (SongInput song : user.getLikedSongs()) {
                    songNames.add(song.getName());
                }
            }
        }

        showPreferredSongsOutput.putPOJO("result", songNames);

        return showPreferredSongsOutput;
    }

    /**
     * Main method call for doRepeat command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param player The array that keeps all user players in check
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doRepeat(final ObjectMapper objectMapper,
                                      final Command crtCommand,
                                      final ArrayList<ItemSelection> player,
                                      final ArrayList<PodcastSelection> podcasts,
                                      final LibraryInput library) {
        ObjectNode repeatOutput = objectMapper.createObjectNode();

        repeatOutput.put("command", "repeat");
        repeatOutput.put("user", crtCommand.getUsername());
        repeatOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    repeatOutput.put("message", user.getUsername() + " is offline.");
                    repeatOutput.putPOJO("results", new ArrayList<>());

                    return repeatOutput;
                }
            }
        }

        //  First we gather the user's player
        ItemSelection crtItem = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                crtItem = item;
                break;
            }
        }
        String message = "";

        if (crtItem == null) {
            //  No player was found
            message = "Please load a source before setting the repeat status.";
            repeatOutput.put("message", message);
        } else {
            //  Player was found and repeat state will be changed

            //  First we update the time
            updatePlayer(player, crtCommand, podcasts, library);

            if (crtItem instanceof PlaylistSelection) {
                switch (crtItem.getRepeat()) {
                    case "No Repeat" -> {
                        crtItem.setRepeat("Repeat All");
                        message = "Repeat mode changed to repeat all.";
                    }
                    case "Repeat All" -> {
                        crtItem.setRepeat("Repeat Current Song");
                        message = "Repeat mode changed to repeat current song.";

                        //  Setting intervals for the song loop
                        PlaylistSelection copy = (PlaylistSelection) crtItem;
                        PlaylistSelection.setIntervals(copy);
                    }
                    case "Repeat Current Song" -> {
                        crtItem.setRepeat("No Repeat");
                        message = "Repeat mode changed to no repeat.";

                        //  Reset intervals
                        PlaylistSelection copy = (PlaylistSelection) crtItem;
                        copy.setStartTimestamp(-1);
                        copy.setStopTimestamp(-1);
                    }
                    default -> {
                    }
                }
            } else {
                switch (crtItem.getRepeat()) {
                    case "No Repeat" -> {
                        crtItem.setRepeat("Repeat Once");
                        message = "Repeat mode changed to repeat once.";
                    }
                    case "Repeat Once" -> {
                        crtItem.setRepeat("Repeat Infinite");
                        message = "Repeat mode changed to repeat infinite.";
                    }
                    case "Repeat Infinite" -> {
                        crtItem.setRepeat("No Repeat");
                        message = "Repeat mode changed to no repeat.";
                    }
                    default -> {
                    }
                }
            }
            repeatOutput.put("message", message);
        }

        return repeatOutput;
    }

    /**
     * Main method call for doShuffle command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param player The array that keeps all user players in check
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doShuffle(final ObjectMapper objectMapper,
                                       final Command crtCommand,
                                       final ArrayList<ItemSelection> player,
                                       final ArrayList<PodcastSelection> podcasts,
                                       final LibraryInput library) {
        ObjectNode shuffleOutput = objectMapper.createObjectNode();

        shuffleOutput.put("command", "shuffle");
        shuffleOutput.put("user", crtCommand.getUsername());
        shuffleOutput.put("timestamp", crtCommand.getTimestamp());

        //  Update player first
        updatePlayer(player, crtCommand, podcasts, library);

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    shuffleOutput.put("message", user.getUsername() + " is offline.");
                    shuffleOutput.putPOJO("results", new ArrayList<>());

                    return shuffleOutput;
                }
            }
        }

        //  Then we gather the user's player
        ItemSelection crtItem = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                crtItem = item;
                break;
            }
        }

        if (crtItem == null) {
            //  No player was found
            shuffleOutput.put("message", "Please load a source "
                    + "before using the shuffle function.");
        } else {
            if (!(crtItem instanceof PlaylistSelection copyItem)) {
                //  Loaded source is not a playlist
                shuffleOutput.put("message", "The loaded source is not a playlist.");
            } else {
                //  All conditions met. Switch to shuffle/unshuffle

                //  Set the message and update playlist
                String message = GetMessages.getShuffleMessage(copyItem, crtCommand);

                shuffleOutput.put("message", message);
            }
        }

        return shuffleOutput;
    }

    /**
     * Main method call for doForward command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @param player The array that keeps all user players in check
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doForward(final ObjectMapper objectMapper,
                                       final Command crtCommand,
                                       final ArrayList<PodcastSelection> podcasts,
                                       final ArrayList<ItemSelection> player,
                                       final LibraryInput library) {
        ObjectNode forwardOutput = objectMapper.createObjectNode();

        forwardOutput.put("command", "forward");
        forwardOutput.put("user", crtCommand.getUsername());
        forwardOutput.put("timestamp", crtCommand.getTimestamp());

        //  First we update the player
        updatePlayer(player, crtCommand, podcasts, library);

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    forwardOutput.put("message", user.getUsername() + " is offline.");
                    forwardOutput.putPOJO("results", new ArrayList<>());

                    return forwardOutput;
                }
            }
        }

        //  Now we check for podcast
        ItemSelection crtItem = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                crtItem = item;
            }
        }

        //  Get message and make changes
        String message = GetMessages.getForwardMessage(crtItem, crtCommand,
                player, podcasts);
        forwardOutput.put("message", message);

        return forwardOutput;
    }

    /**
     * Main method call for doBackward command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @param player The array that keeps all user players in check
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doBackward(final ObjectMapper objectMapper,
                                        final Command crtCommand,
                                        final ArrayList<PodcastSelection> podcasts,
                                        final ArrayList<ItemSelection> player,
                                        final LibraryInput library) {
        ObjectNode backwardOutput = objectMapper.createObjectNode();

        backwardOutput.put("command", "backward");
        backwardOutput.put("user", crtCommand.getUsername());
        backwardOutput.put("timestamp", crtCommand.getTimestamp());

        //  First we update the player
        updatePlayer(player, crtCommand, podcasts, library);

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    backwardOutput.put("message", user.getUsername() + " is offline.");
                    backwardOutput.putPOJO("results", new ArrayList<>());

                    return backwardOutput;
                }
            }
        }

        //  Now we check for podcast
        ItemSelection crtItem = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                crtItem = item;
            }
        }

        //  Get message and make changes
        String message = GetMessages.getBackwardMessage(crtItem);
        backwardOutput.put("message", message);

        return backwardOutput;
    }

    /**
     * Main method call for doNext command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @param player The array that keeps all user players in check
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doNext(final ObjectMapper objectMapper,
                                    final Command crtCommand,
                                    final ArrayList<PodcastSelection> podcasts,
                                    final ArrayList<ItemSelection> player,
                                    final LibraryInput library) {
        ObjectNode nextOutput = objectMapper.createObjectNode();

        nextOutput.put("command", "next");
        nextOutput.put("user", crtCommand.getUsername());
        nextOutput.put("timestamp", crtCommand.getTimestamp());

        //  First we update the player
        updatePlayer(player, crtCommand, podcasts, library);

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    nextOutput.put("message", user.getUsername() + " is offline.");
                    nextOutput.putPOJO("results", new ArrayList<>());

                    return nextOutput;
                }
            }
        }

        //  Now we check for loaded source
        ItemSelection crtItem = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                crtItem = item;
            }
        }

        //  Get message and make changes
        String message = GetMessages.getNextMessage(crtItem, player,
                podcasts, crtCommand);
        nextOutput.put("message", message);

        return nextOutput;
    }

    /**
     * Main method call for doPrev command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @param player The array that keeps all user players in check
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doPrev(final ObjectMapper objectMapper,
                                    final Command crtCommand,
                                    final ArrayList<PodcastSelection> podcasts,
                                    final ArrayList<ItemSelection> player,
                                    final LibraryInput library) {
        ObjectNode prevOutput = objectMapper.createObjectNode();

        prevOutput.put("command", "prev");
        prevOutput.put("user", crtCommand.getUsername());
        prevOutput.put("timestamp", crtCommand.getTimestamp());

        //  First we update the player
        updatePlayer(player, crtCommand, podcasts, library);

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    prevOutput.put("message", user.getUsername() + " is offline.");
                    prevOutput.putPOJO("results", new ArrayList<>());

                    return prevOutput;
                }
            }
        }

        //  Now we check for loaded source
        ItemSelection crtItem = null;

        for (ItemSelection item : player) {
            if (item.getUser().equals(crtCommand.getUsername())) {
                crtItem = item;
            }
        }

        //  Get message and make changes
        String message = GetMessages.getPrevMessage(crtItem, crtCommand);
        prevOutput.put("message", message);

        return prevOutput;
    }

    /**
     * Main method call for doFollow command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param steps The array that checks whether search and select were executed
     * @param lastSearchResult The array containing the search result and its type
     * @param playlists The array of all user playlists
     * @param usersPlaylists The array of users and their respective playlists
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doFollow(final ObjectMapper objectMapper,
                                      final Command crtCommand,
                                      final int[] steps,
                                      final ArrayList<String> lastSearchResult,
                                      final ArrayList<Playlist> playlists,
                                      final ArrayList<UserPlaylists> usersPlaylists,
                                      final LibraryInput library) {
        ObjectNode followOutput = objectMapper.createObjectNode();

        followOutput.put("command", "follow");
        followOutput.put("user", crtCommand.getUsername());
        followOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    followOutput.put("message", user.getUsername() + " is offline.");
                    followOutput.putPOJO("results", new ArrayList<>());

                    return followOutput;
                }
            }
        }

        //  Adding the appropriate load message
        //  No select or no results found
        if (steps[1] == 0) {
            followOutput.put("message", "Please select a source "
                    + "before following or unfollowing.");

            //  Source is not a playlist
        } else if (!lastSearchResult.get(0).equals("playlist")) {
            followOutput.put("message", "The selected source is not a playlist.");

        } else {
            //  Localize the specific playlist
            Playlist wantedPlaylist = null;
            for (Playlist playlist : playlists) {
                if (playlist.getName().equals(lastSearchResult.get(1))) {
                    wantedPlaylist = playlist;
                    break;
                }
            }

            //  User tries to follow their own playlist
            if (wantedPlaylist.getOwner().equals(crtCommand.getUsername())) {
                followOutput.put("message", "You cannot follow "
                        + "or unfollow your own playlist.");

                //  The follow/unfollow command can be executed
            } else {
                String message = GetMessages.getFollowMessage(wantedPlaylist,
                        crtCommand, usersPlaylists);
                followOutput.put("message", message);
            }

            //  Clearing the result so that we can't follow it twice
            lastSearchResult.clear();
            //  Reset steps
            steps[0] = 0;
            steps[1] = 0;
        }

        return followOutput;
    }

    /**
     * Main method call for doSwitchVisibility command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param usersPlaylists The array of users and their respective playlists
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSwitchVisibility(final ObjectMapper objectMapper,
                                                final Command crtCommand,
                                                final ArrayList<UserPlaylists> usersPlaylists,
                                                final LibraryInput library) {
        ObjectNode switchOutput = objectMapper.createObjectNode();

        switchOutput.put("command", "switchVisibility");
        switchOutput.put("user", crtCommand.getUsername());
        switchOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    switchOutput.put("message", user.getUsername() + " is offline.");
                    switchOutput.putPOJO("results", new ArrayList<>());

                    return switchOutput;
                }
            }
        }

        //  Get message and make changes
        String message = GetMessages.getSwitchVisibilityMessage(usersPlaylists,
                crtCommand);
        switchOutput.put("message", message);

        return switchOutput;
    }

    /**
     * Main method call for doGetTop5Songs command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param songsLikes The array of songs and their respective likes
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doGetTop5Songs(final ObjectMapper objectMapper,
                                            final Command crtCommand,
                                            final ArrayList<SongLikes> songsLikes) {
        ObjectNode topSongsOutput = objectMapper.createObjectNode();

        topSongsOutput.put("command", "getTop5Songs");
        topSongsOutput.put("timestamp", crtCommand.getTimestamp());

        //  Sort the songs in a separate array and then take the first 5 results
        ArrayList<SongLikes> sortedSongsByLikes = new ArrayList<>(songsLikes);
        sortedSongsByLikes.sort(Collections.reverseOrder());

        //  Truncate the result to 5
        if (sortedSongsByLikes.size() > Constants.MAX_SIZE_5) {
            sortedSongsByLikes.subList(Constants.MAX_SIZE_5,
                    sortedSongsByLikes.size()).clear();
        }

        //  Store names
        ArrayList<String> result = new ArrayList<>();
        for (SongLikes songLikes : sortedSongsByLikes) {
            result.add(songLikes.getSong().getName());
        }

        topSongsOutput.putPOJO("result", result);

        return topSongsOutput;
    }

    /**
     * Main method call for doGetTop5Playlists command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param playlists The array of all user playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doGetTop5Playlists(final ObjectMapper objectMapper,
                                                final Command crtCommand,
                                                final ArrayList<Playlist> playlists) {
        ObjectNode topPlaylistsOutput = objectMapper.createObjectNode();

        topPlaylistsOutput.put("command", "getTop5Playlists");
        topPlaylistsOutput.put("timestamp", crtCommand.getTimestamp());

        //  Sort the songs in a separate array and then take the first 5 results
        ArrayList<Playlist> sortedPlaylists = new ArrayList<>(playlists);
        sortedPlaylists.sort(Collections.reverseOrder());

        //  Truncate the result to 5
        if (sortedPlaylists.size() > Constants.MAX_SIZE_5) {
            sortedPlaylists.subList(Constants.MAX_SIZE_5,
                    sortedPlaylists.size()).clear();
        }

        //  Store names
        ArrayList<String> result = new ArrayList<>();
        for (Playlist playlist : sortedPlaylists) {
            result.add(playlist.getName());
        }

        topPlaylistsOutput.putPOJO("result", result);

        return topPlaylistsOutput;
    }

    /**
     * Main method call for doSwitchConnectionStatus command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param player The array that keeps all user players in check
     * @param library Singleton containing all songs, users and podcasts
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSwitchConnectionStatus(final ObjectMapper objectMapper,
                                                      final Command crtCommand,
                                                      final ArrayList<ItemSelection> player,
                                                      final LibraryInput library,
                                                      final ArrayList<PodcastSelection> podcasts) {
        ObjectNode switchConnectionOutput = objectMapper.createObjectNode();

        switchConnectionOutput.put("command", "switchConnectionStatus");
        switchConnectionOutput.put("user", crtCommand.getUsername());
        switchConnectionOutput.put("timestamp", crtCommand.getTimestamp());

        UserInput crtUser = null;

        //  Searching for the current user
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                crtUser = user;
                break;
            }
        }

        //  Get message and make changes
        String message = getSwitchConnectionMessage(crtUser,
                player, crtCommand, podcasts, library);
        switchConnectionOutput.put("message", message);

        return  switchConnectionOutput;
    }

    /**
     * Main method call for doGetOnlineUsers command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doGetOnlineUsers(final ObjectMapper objectMapper,
                                              final Command crtCommand,
                                              final LibraryInput library) {
        ObjectNode getUsersOutput = objectMapper.createObjectNode();
        getUsersOutput.put("command", "getOnlineUsers");
        getUsersOutput.put("timestamp", crtCommand.getTimestamp());

        ArrayList<String> onlineUsers = new ArrayList<>();

        //  Filter online users and add them to the result array
        for (UserInput user : library.getUsers()) {
            if (user.getType().equals("user")
                    && user.isOnline()) {
                onlineUsers.add(user.getUsername());
            }
        }

        getUsersOutput.putPOJO("result", onlineUsers);

        return getUsersOutput;
    }

    /**
     * Main method call for doAddUser command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param library Singleton containing all songs, users and podcasts
     * @param usersPlaylists The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doAddUser(final ObjectMapper objectMapper,
                                       final Command crtCommand,
                                       final LibraryInput library,
                                       final ArrayList<UserPlaylists> usersPlaylists) {
        ObjectNode addUserOutput = objectMapper.createObjectNode();

        addUserOutput.put("command", "addUser");
        addUserOutput.put("user", crtCommand.getUsername());
        addUserOutput.put("timestamp", crtCommand.getTimestamp());

        String message = GetMessages.getAddUserMessage(crtCommand,
                library, usersPlaylists);
        addUserOutput.put("message", message);

        return addUserOutput;
    }

    /**
     * Main method call for doAddAlbum command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param library Singleton containing all songs, users and podcasts
     * @param usersPlaylists The array of users and their respective playlists
     * @param albums The array of all albums in the database
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doAddAlbum(final ObjectMapper objectMapper,
                                        final Command crtCommand,
                                        final LibraryInput library,
                                        final ArrayList<UserPlaylists> usersPlaylists,
                                        final ArrayList<Album> albums) {
        ObjectNode addUserOutput = objectMapper.createObjectNode();

        addUserOutput.put("command", "addAlbum");
        addUserOutput.put("user", crtCommand.getUsername());
        addUserOutput.put("timestamp", crtCommand.getTimestamp());

        String message = GetMessages.getAddAlbumMessage(crtCommand, library,
                usersPlaylists, albums);
        addUserOutput.put("message", message);

        return addUserOutput;
    }

    /**
     * Main method call for doShowAlbums command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param usersPlaylists The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doShowAlbums(final ObjectMapper objectMapper,
                                          final Command crtCommand,
                                          final ArrayList<UserPlaylists> usersPlaylists) {
        ObjectNode showAlbumsOutput = objectMapper.createObjectNode();

        showAlbumsOutput.put("command", "showAlbums");
        showAlbumsOutput.put("user", crtCommand.getUsername());
        showAlbumsOutput.put("timestamp", crtCommand.getTimestamp());

        UserPlaylists crtUser = null;

        //  Search for the user's playlists
        for (UserPlaylists userPlaylists : usersPlaylists) {
            String username = userPlaylists.getUser().getUsername();
            if (username.equals(crtCommand.getUsername())) {
                crtUser = userPlaylists;
                break;
            }
        }

        ArrayList<ObjectNode> result = new ArrayList<>();

        if (crtUser != null) {
            for (Album album : crtUser.getAlbums()) {
                ObjectNode resultNode = objectMapper.createObjectNode();

                //  Set album data
                resultNode.put("name", album.getName());

                ArrayList<String> songNames = new ArrayList<>();
                ArrayList<SongInput> songs = album.getSongs();

                for (SongInput song : songs) {
                    songNames.add(song.getName());
                }
                resultNode.putPOJO("songs", songNames);

                result.add(resultNode);
            }

            showAlbumsOutput.putPOJO("result", result);
        }

        return showAlbumsOutput;
    }
}
