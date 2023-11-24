package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(CheckerConstants.TESTS_PATH + file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput  for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        ArrayNode outputs = objectMapper.createArrayNode();

        // TODO add your implementation

        //  Reading commands from input file
        final ArrayList<Command> commands = objectMapper.readValue(
                new File(filePathInput),
                new TypeReference<ArrayList<Command>>() {
                }
        );

        //  IMPORTANT VARIABLES DECLARATION STARTS HERE

        //  Storing last result and checking if it was initialized
        ArrayList<String> lastSearchResult = new ArrayList<>();

        //  Storing if search and select were called before a load was called
        int[] steps = new int[2];

        //  Storing all selections in an array
        ArrayList<ItemSelection> player = new ArrayList<>();

        //  Creating an array list of all the playlists
        ArrayList<Playlist> playlists = new ArrayList<>();

        //  Creating an array list of playlists sorted by users
        ArrayList<UserPlaylists> usersPlaylists = new ArrayList<>();

        for (UserInput user : library.getUsers()) {
            UserPlaylists newUserPlaylists = new UserPlaylists();
            newUserPlaylists.setUser(user);

            usersPlaylists.add(newUserPlaylists);
        }

        //  Keeping played podcasts in order for the user to easily resume them
        ArrayList<PodcastSelection> podcasts = new ArrayList<>();

        //  Storing all songs with their respective likes
        ArrayList<SongLikes> songsLikes = new ArrayList<>();

        for (SongInput song : library.getSongs()) {
            SongLikes newSongLikes = new SongLikes();
            newSongLikes.setSong(song);

            songsLikes.add(newSongLikes);
        }

        //  IMPORTANT VARIABLES DECLARATION ENDS HERE

        //  Parsing commands
        for (Command crtCommand : commands) {
            switch (crtCommand.getCommand()) {
                //  SEARCH COMMAND
                case "search" -> {
                    //  Update all players first
                    updatePlayer(player, crtCommand, podcasts);

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

                    //  Searching for a song
                    ObjectNode searchOutput = objectMapper.createObjectNode();

                    //  Setting the output
                    searchOutput.put("command", "search");
                    searchOutput.put("user", crtCommand.getUsername());
                    searchOutput.put("timestamp", crtCommand.getTimestamp());

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

                            outputs.add(searchOutput);
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

                            outputs.add(searchOutput);
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

                            outputs.add(searchOutput);
                        }

                        default -> throw new IllegalStateException("Unexpected value: "
                                + crtCommand.getType());
                    }
                }

                case "select" -> {
                    //  Setting the output
                    ObjectNode selectOutput = objectMapper.createObjectNode();
                    selectOutput.put("command", "select");
                    selectOutput.put("user", crtCommand.getUsername());
                    selectOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Creating the message
                    String message = GetMessages.getSelectMessage(lastSearchResult,
                            crtCommand, steps);
                    selectOutput.put("message", message);

                    outputs.add(selectOutput);

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
                }

                case "load" -> {
                    ObjectNode loadOutput = objectMapper.createObjectNode();
                    loadOutput.put("command", "load");
                    loadOutput.put("user", crtCommand.getUsername());
                    loadOutput.put("timestamp", crtCommand.getTimestamp());

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

                    outputs.add(loadOutput);
                }

                case "status" -> {
                    ObjectNode statusOutput = objectMapper.createObjectNode();
                    statusOutput.put("command", "status");
                    statusOutput.put("user", crtCommand.getUsername());
                    statusOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Update the player
                    updatePlayer(player, crtCommand, podcasts);

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

                    outputs.add(statusOutput);
                }

                case "playPause" -> {
                    ObjectNode playPauseOutput = objectMapper.createObjectNode();

                    playPauseOutput.put("command", "playPause");
                    playPauseOutput.put("user", crtCommand.getUsername());
                    playPauseOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Update the player
                    updatePlayer(player, crtCommand, podcasts);

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

                    outputs.add(playPauseOutput);
                }

                case "createPlaylist" -> {
                    ObjectNode createPlaylistOutput = objectMapper.createObjectNode();

                    createPlaylistOutput.put("command", "createPlaylist");
                    createPlaylistOutput.put("user", crtCommand.getUsername());
                    createPlaylistOutput.put("timestamp", crtCommand.getTimestamp());

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

                    outputs.add(createPlaylistOutput);
                }

                case "addRemoveInPlaylist" -> {
                    ObjectNode addRemoveOutput = objectMapper.createObjectNode();

                    addRemoveOutput.put("command", "addRemoveInPlaylist");
                    addRemoveOutput.put("user", crtCommand.getUsername());
                    addRemoveOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Get message and make proper modifications to the playlist
                    String message = GetMessages.getAddRemoveMessage(player, playlists, crtCommand);
                    addRemoveOutput.put("message", message);

                    outputs.add(addRemoveOutput);
                }

                case "like" -> {
                    ObjectNode likeOutput = objectMapper.createObjectNode();

                    likeOutput.put("command", "like");
                    likeOutput.put("user", crtCommand.getUsername());
                    likeOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Get message and make proper modifications to the user's liked songs
                    String message = GetMessages.getLikeMessage(player, usersPlaylists,
                            crtCommand, songsLikes);

                    likeOutput.put("message", message);

                    outputs.add(likeOutput);
                }

                case "showPlaylists" -> {
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

                    outputs.add(showPlaylistsOutput);
                }

                case "showPreferredSongs" -> {
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

                    outputs.add(showPreferredSongsOutput);
                }

                case "repeat" -> {
                    ObjectNode repeatOutput = objectMapper.createObjectNode();

                    repeatOutput.put("command", "repeat");
                    repeatOutput.put("user", crtCommand.getUsername());
                    repeatOutput.put("timestamp", crtCommand.getTimestamp());

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
                        updatePlayer(player, crtCommand, podcasts);

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

                    outputs.add(repeatOutput);
                }

                case "shuffle" -> {
                    ObjectNode shuffleOutput = objectMapper.createObjectNode();

                    shuffleOutput.put("command", "shuffle");
                    shuffleOutput.put("user", crtCommand.getUsername());
                    shuffleOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Update player first
                    updatePlayer(player, crtCommand, podcasts);

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

                    outputs.add(shuffleOutput);
                }

                case "forward" -> {
                    ObjectNode forwardOutput = objectMapper.createObjectNode();

                    forwardOutput.put("command", "forward");
                    forwardOutput.put("user", crtCommand.getUsername());
                    forwardOutput.put("timestamp", crtCommand.getTimestamp());

                    //  First we update the player
                    updatePlayer(player, crtCommand, podcasts);

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

                    outputs.add(forwardOutput);
                }

                case "backward" -> {
                    ObjectNode backwardOutput = objectMapper.createObjectNode();

                    backwardOutput.put("command", "backward");
                    backwardOutput.put("user", crtCommand.getUsername());
                    backwardOutput.put("timestamp", crtCommand.getTimestamp());

                    //  First we update the player
                    updatePlayer(player, crtCommand, podcasts);

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

                    outputs.add(backwardOutput);
                }

                case "next" -> {
                    ObjectNode nextOutput = objectMapper.createObjectNode();

                    nextOutput.put("command", "next");
                    nextOutput.put("user", crtCommand.getUsername());
                    nextOutput.put("timestamp", crtCommand.getTimestamp());

                    //  First we update the player
                    updatePlayer(player, crtCommand, podcasts);

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

                    outputs.add(nextOutput);
                }

                case "prev" -> {
                    ObjectNode prevOutput = objectMapper.createObjectNode();

                    prevOutput.put("command", "prev");
                    prevOutput.put("user", crtCommand.getUsername());
                    prevOutput.put("timestamp", crtCommand.getTimestamp());

                    //  First we update the player
                    updatePlayer(player, crtCommand, podcasts);

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

                    outputs.add(prevOutput);
                }

                case "follow" -> {
                    ObjectNode followOutput = objectMapper.createObjectNode();
                    followOutput.put("command", "follow");
                    followOutput.put("user", crtCommand.getUsername());
                    followOutput.put("timestamp", crtCommand.getTimestamp());

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

                    outputs.add(followOutput);
                }

                case "switchVisibility" -> {
                    ObjectNode switchOutput = objectMapper.createObjectNode();

                    switchOutput.put("command", "switchVisibility");
                    switchOutput.put("user", crtCommand.getUsername());
                    switchOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Get message and make changes
                    String message = GetMessages.getSwitchVisibilityMessage(usersPlaylists,
                            crtCommand);
                    switchOutput.put("message", message);

                    outputs.add(switchOutput);
                }

                case "getTop5Songs" -> {
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

                    outputs.add(topSongsOutput);

                }

                case "getTop5Playlists" -> {
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

                    outputs.add(topPlaylistsOutput);
                }
                default -> {
                }
            }
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }

    /**
     * This method updates the current time of all unpaused songs in the player
     *
     * @param player The array that keeps all user players in check
     * @param crtCommand The current command with all its data
     * @param podcasts The array that keeps track of all the podcasts
     *                 when they are not loaded
     */
    public static void updatePlayer(final ArrayList<ItemSelection> player,
                                    final Command crtCommand,
                                    final ArrayList<PodcastSelection> podcasts) {
        //  Iterate through the player and update times
        //  Remove all finished sources
        ArrayList<ItemSelection> removableItems = new ArrayList<>();

        for (ItemSelection item : player) {
            item.updateRemainingTime(crtCommand.getTimestamp());

            if (item.getRemainingTime() == 0) {
                if (item instanceof PodcastSelection) {
                    podcasts.remove(item);
                }
                removableItems.add(item);
            }
        }

        for (ItemSelection item : removableItems) {
            player.remove(item);
        }

        removableItems.clear();
    }

}

