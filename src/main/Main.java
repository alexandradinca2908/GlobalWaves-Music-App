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
import java.util.Arrays;
import java.util.List;
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
     * @param filePathInput for input file
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
        ArrayList<Command> commands = objectMapper.readValue(
                new File(filePathInput),
                new TypeReference<ArrayList<Command>>() {}
        );

        //  IMPORTANT VARIABLES DECLARATION STARTS HERE

        //  Storing last result and checking if it was initialized
        ArrayList<String> lastSearchResult = new ArrayList<>();
        boolean initLastSearchResult = false;

        //  Storing all selections in an array
        ArrayList<ItemSelection> player = new ArrayList<>();

        //  Keeping last selection for loading
        LastSelection lastSelection = new LastSelection();

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

        //  IMPORTANT VARIABLES DECLARATION ENDS HERE

        //  Parsing commands
        for (Command crtCommand : commands) {
            switch(crtCommand.getCommand()) {
                //  SEARCH COMMAND
                case "search" -> {
                    //  Clear player first
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
                            searchForSongs(filters, result, library);

                            //  Truncate results if needed
                            if (result.size() > 5) {
                                result.subList(5, result.size()).clear();
                            }

                            //  Setting the message
                            searchOutput.put("message", "Search returned " + result.size() + " results");

                            //  Extracting the names of the songs
                            ArrayList<String > songNames = new ArrayList<>();
                            for (SongInput songInput : result) {
                                songNames.add(songInput.getName());
                            }
                            searchOutput.putPOJO("results", songNames);

                            //  Storing the result in case we need to select it later
                            storeResultForSelect(lastSearchResult, lastSelection, songNames, "song");
                            initLastSearchResult = true;

                            outputs.add(searchOutput);
                        }

                        case "playlist" -> {
                            Filters filters = crtCommand.getFilters();
                            ArrayList<Playlist> result = new ArrayList<>();

                            //  Found playlists will be added in result array
                            searchForPlaylists(filters, result, playlists);

                            //  Taking out private playlists
                            result.removeIf(playlist -> !playlist.getOwner().equals(crtCommand.getUsername())
                                    && !playlist.isVisibility());

                            //  Truncate results if needed
                            if (result.size() > 5) {
                                result.subList(5, result.size()).clear();
                            }

                            //  Setting the message
                            searchOutput.put("message", "Search returned " + result.size() + " results");

                            //  Extracting the names of the playlists
                            ArrayList<String > playlistNames = new ArrayList<>();
                            for (Playlist playlist : result) {
                                playlistNames.add(playlist.getName());
                            }
                            searchOutput.putPOJO("results", playlistNames);

                            //  Storing the result in case we need to select it later
                            storeResultForSelect(lastSearchResult, lastSelection, playlistNames, "playlist");
                            initLastSearchResult = true;

                            outputs.add(searchOutput);
                        }

                        case "podcast" -> {
                            Filters filters = crtCommand.getFilters();
                            ArrayList<PodcastInput> result = new ArrayList<>();

                            //  Found podcasts will be added in result array
                            searchForPodcasts(filters, result, library);

                            //  Truncate results if needed
                            if (result.size() > 5) {
                                result.subList(5, result.size()).clear();
                            }

                            //  Setting the message
                            searchOutput.put("message", "Search returned " + result.size() + " results");

                            //  Extracting the names of the podcasts
                            ArrayList<String > podcastNames = new ArrayList<>();
                            for (PodcastInput podcastInput : result) {
                                podcastNames.add(podcastInput.getName());
                            }
                            searchOutput.putPOJO("results", podcastNames);

                            //  Storing the result in case we need to select it later
                            storeResultForSelect(lastSearchResult, lastSelection, podcastNames, "podcast");
                            initLastSearchResult = true;

                            outputs.add(searchOutput);
                        }

                        default -> throw new IllegalStateException("Unexpected value: " + crtCommand.getType());
                    }
                }

                case "select" -> {
                    //  Setting the output
                    ObjectNode selectOutput = objectMapper.createObjectNode();
                    selectOutput.put("command", "select");
                    selectOutput.put("user", crtCommand.getUsername());
                    selectOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Creating the message
                    String message = getSelectMessage(lastSearchResult, crtCommand);
                    selectOutput.put("message", message);

                    outputs.add(selectOutput);

                    //  Storing the selection in case we need to load it
                    if (message.contains("Successfully selected")) {
                        int index = crtCommand.getItemNumber();
                        lastSelection.setSelectionName(lastSearchResult.get(index));
                        lastSelection.setSelectionType(lastSearchResult.get(0));
                    }
                }

                case "load" -> {
                    ObjectNode loadOutput = objectMapper.createObjectNode();
                    loadOutput.put("command", "load");
                    loadOutput.put("user", crtCommand.getUsername());
                    loadOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Adding the appropriate load message
                    if (lastSearchResult.isEmpty()) {
                        if (initLastSearchResult) {
                            loadOutput.put("message", "You can't load an empty audio collection!");
                        } else {
                            loadOutput.put("message", "Please select a source before attempting to load.");
                        }
                    } else {
                        loadOutput.put("message", "Playback loaded successfully.");

                        //  Loading the song into the database
                        if (lastSelection.getSelectionType().equals("song")) {
                            SongSelection selectedSong = getSongSelection(crtCommand, library, lastSelection);

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
                        if (lastSelection.getSelectionType().equals("playlist")) {
                            PlaylistSelection selectedPlaylist = getPlaylistSelection(crtCommand, playlists, lastSelection);

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
                        if (lastSelection.getSelectionType().equals("podcast")) {
                            PodcastSelection selectedPodcast = getPodcastSelection(crtCommand, library, lastSelection);

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
                    }

                    outputs.add(loadOutput);
                }

                case "status" -> {
                    ObjectNode statusOutput = objectMapper.createObjectNode();
                    statusOutput.put("command", "status");
                    statusOutput.put("user", crtCommand.getUsername());
                    statusOutput.put("timestamp", crtCommand.getTimestamp());

                    String user = crtCommand.getUsername();
                    ItemSelection reqItem = new ItemSelection();

                    for (ItemSelection item : player) {
                        if (item.getUser().equals(user)) {
                            reqItem = item;
                        }
                    }

                    //  Setting the stats
                    ObjectNode stats = getStats(reqItem, objectMapper, crtCommand);
                    statusOutput.set("stats", stats);

                    outputs.add(statusOutput);
                }

                case "playPause" -> {
                    ObjectNode playPauseOutput = objectMapper.createObjectNode();

                    playPauseOutput.put("command", "playPause");
                    playPauseOutput.put("user", crtCommand.getUsername());
                    playPauseOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Looking for what the user is playing
                    int found = 0;
                    for (ItemSelection item : player) {
                        if (item.getUser().equals(crtCommand.getUsername())) {
                            if (item.isPaused()) {
                                playPauseOutput.put("message", "Playback resumed successfully.");
                                item.setPaused(false);
                                found = 1;

                                //  Updating start time
                                item.setStartTime(crtCommand.getTimestamp());

                                break;
                            } else {
                                playPauseOutput.put("message", "Playback paused successfully.");
                                item.setPaused(true);
                                found = 1;

                                //  Updating remaining time
                                int remainingTime = item.getRemainingTime() - (crtCommand.getTimestamp() - item.getStartTime());
                                item.setRemainingTime(remainingTime);

                                break;
                            }
                        }
                    }

                    if (found == 0) {
                        playPauseOutput.put("message", "Please load a source before attempting to pause or resume playback.");
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
                                createPlaylistOutput.put("message", "A playlist with the same name already exists.");
                                exists = 1;

                                break;
                            }
                        }
                    }

                    if (exists == 0) {
                        Playlist newPlaylist = new Playlist(crtCommand.getPlaylistName(), crtCommand.getUsername());
                        //  Add playlist in general list
                        playlists.add(newPlaylist);
                        //  Add playlist in user's list
                        for (UserPlaylists userPlaylists : usersPlaylists) {
                            if (userPlaylists.getUser().getUsername().equals(crtCommand.getUsername())) {
                                userPlaylists.getPlaylists().add(newPlaylist);
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
                    String message = getAddRemoveMessage(player, playlists, usersPlaylists, crtCommand);
                    addRemoveOutput.put("message", message);

                    outputs.add(addRemoveOutput);
                }

                case "like" -> {
                    ObjectNode likeOutput = objectMapper.createObjectNode();

                    likeOutput.put("command", "like");
                    likeOutput.put("user", crtCommand.getUsername());
                    likeOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Get message and make proper modifications to the user's liked songs
                    String message = getLikeMessage(player, usersPlaylists, crtCommand);

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

                    for (UserPlaylists userPlaylists : usersPlaylists) {
                        if (userPlaylists.getUser().getUsername().equals(crtCommand.getUsername())) {
                            user = userPlaylists;
                            break;
                        }
                    }

                    if (user != null) {
                        for (Playlist playlist : user.getPlaylists()) {
                            ObjectNode resultNode = objectMapper.createObjectNode();

                            //  Set playlist data
                            resultNode.put("name", playlist.getName());

                            ArrayList<String> songNames = new ArrayList<>();
                            for (SongInput song : playlist.getSongs()) {
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
                    ObjectNode showPreferredSongs = objectMapper.createObjectNode();

                    showPreferredSongs.put("command", "showPreferredSongs");
                    showPreferredSongs.put("user", crtCommand.getUsername());
                    showPreferredSongs.put("timestamp", crtCommand.getTimestamp());

                    ArrayList<String> songNames = new ArrayList<>();

                    for (UserPlaylists userPlaylists :usersPlaylists) {
                        if (userPlaylists.getUser().getUsername().equals(crtCommand.getUsername())) {
                            for (SongInput song : userPlaylists.getLikedSongs()) {
                                songNames.add(song.getName());
                            }
                        }
                    }

                    showPreferredSongs.putPOJO("result", songNames);

                    outputs.add(showPreferredSongs);
                }

                default -> {
                    break;
                }
            }
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }

    //  Search command function that filters all songs
    public static void searchForSongs(Filters filters, ArrayList<SongInput> result, LibraryInput library) {
        //  Add all songs containing the searched name
        if (filters.getName() != null) {
            for (SongInput song : library.getSongs()) {
                if (song.getName().startsWith(filters.getName())) {
                    result.add(song);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from unwanted albums
        if (result.isEmpty()) {
            if (filters.getAlbum() != null) {
                for (SongInput song : library.getSongs()) {
                    if (song.getAlbum().equals(filters.getAlbum())) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getAlbum() != null) {
                result.removeIf(song -> !song.getAlbum().equals(filters.getAlbum()));
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs with unwanted tags
        if (result.isEmpty()) {
            if (filters.getTags() != null) {
                for (SongInput song : library.getSongs()) {
                    int hasTags = 1;
                    for (String tag : filters.getTags()) {
                        if (!song.getTags().contains(tag)) {
                            hasTags = 0;
                            break;
                        }
                    }
                    if (hasTags == 1) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getTags() != null) {
                for (SongInput song : result) {
                    for (String tag : filters.getTags()) {
                        if (!song.getTags().contains(tag)) {
                            result.remove(song);
                            break;
                        }
                    }
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs with mismatched lyrics
        if (result.isEmpty()) {
            if (filters.getLyrics() != null) {
                for (SongInput song : library.getSongs()) {
                    if (song.getLyrics().contains(filters.getLyrics())) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getLyrics() != null) {
                result.removeIf(song -> !song.getLyrics().contains(filters.getLyrics()));
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from wrong genre
        if (result.isEmpty()) {
            if (filters.getGenre() != null) {
                for (SongInput song : library.getSongs()) {
                    if (song.getGenre().equalsIgnoreCase(filters.getGenre())) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getGenre() != null) {
                result.removeIf(song -> !song.getGenre().equalsIgnoreCase(filters.getGenre()));
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from wrong years
        if (result.isEmpty()) {
            if (filters.getReleaseYear() != null) {
                //  Extract the </> operator and the year from original filter
                char op = filters.getReleaseYear().charAt(0);
                int year = Integer.parseInt(filters.getReleaseYear().substring(1));

                if (op == '>') {
                    for (SongInput song : library.getSongs()) {
                        if (song.getReleaseYear() > year) {
                            result.add(song);
                        }
                    }
                } else {
                    for (SongInput song : library.getSongs()) {
                        if (song.getReleaseYear() < year) {
                            result.add(song);
                        }
                    }
                }

            }
        } else {
            if (filters.getReleaseYear() != null) {
                //  Extract the </> operator and the year from original filter
                char op = filters.getReleaseYear().charAt(0);
                int year = Integer.parseInt(filters.getReleaseYear().substring(1));

                if (op == '>') {
                    result.removeIf(song -> song.getReleaseYear() < year);
                } else {
                    result.removeIf(song -> song.getReleaseYear() > year);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from other artists
        if (result.isEmpty()) {
            if (filters.getArtist() != null) {
                for (SongInput song : library.getSongs()) {
                    if (song.getArtist().equals(filters.getArtist())) {
                        result.add(song);
                    }
                }
            }
        } else {
            if (filters.getArtist() != null) {
                result.removeIf(song -> !song.getArtist().equals(filters.getArtist()));
            }
        }
    }

    //  Search command function that filters all playlists
    public static void searchForPlaylists(Filters filters, ArrayList<Playlist> result, ArrayList<Playlist> playlists) {
        //  Add all playlists containing the searched name
        if (filters.getName() != null) {
            for (Playlist playlist : playlists) {
                if (playlist.getName().startsWith(filters.getName())) {
                    result.add(playlist);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove playlists from other owners
        if (result.isEmpty()) {
            if (filters.getOwner() != null) {
                for (Playlist playlist : playlists) {
                    if (playlist.getOwner().equals(filters.getOwner())) {
                        result.add(playlist);
                    }
                }
            }
        } else {
            if (filters.getOwner() != null) {
                result.removeIf(playlist -> !playlist.getOwner().equals(filters.getOwner()));
            }
        }
    }

    public static void searchForPodcasts(Filters filters, ArrayList<PodcastInput> result, LibraryInput library) {
        //  Add all playlists containing the searched name
        if (filters.getName() != null) {
            for (PodcastInput podcast : library.getPodcasts()) {
                if (podcast.getName().startsWith(filters.getName())) {
                    result.add(podcast);
                }
            }
        }

        //  Initialize result if it is still empty
        //  Else, parse the array and remove songs from other owners
        if (result.isEmpty()) {
            if (filters.getOwner() != null) {
                for (PodcastInput podcast : library.getPodcasts()) {
                    if (podcast.getOwner().equals(filters.getOwner())) {
                        result.add(podcast);
                    }
                }
            }
        } else {
            if (filters.getOwner() != null) {
                result.removeIf(podcast -> !podcast.getOwner().equals(filters.getOwner()));
            }
        }
    }

    public static String getSelectMessage(ArrayList<String> lastSearchResult, Command crtCommand) {
        String message;
        if (lastSearchResult.isEmpty()) {
            message = "Please conduct a search before making a selection.";
        } else if (crtCommand.getItemNumber() > lastSearchResult.size() - 1) {
            message = "The selected ID is too high.";
        } else {
            int index = crtCommand.getItemNumber();
            message = "Successfully selected " + lastSearchResult.get(index) + ".";
        }

        return message;
    }

    public static SongSelection getSongSelection(Command crtCommand, LibraryInput library, LastSelection lastSelection) {
        SongSelection selectedSong = new SongSelection();
        //  Set song
        for (SongInput song : library.getSongs()) {
            if (song.getName().equals(lastSelection.getSelectionName())) {
                selectedSong.setSong(song);
                break;
            }
        }

        //  Set user
        selectedSong.setUser(crtCommand.getUsername());
        //  Set start time
        selectedSong.setStartTime(crtCommand.getTimestamp());
        //  Set remaining time
        selectedSong.setRemainingTime(selectedSong.getSong().getDuration());

        return selectedSong;
    }

    public static PlaylistSelection getPlaylistSelection(Command crtCommand, ArrayList<Playlist> playlists, LastSelection lastSelection) {
        PlaylistSelection selectedPlaylist = new PlaylistSelection();
        //  Set name
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(lastSelection.getSelectionName())) {
                selectedPlaylist.setPlaylist(playlist);
                break;
            }
        }
        //  Set user
        selectedPlaylist.setUser(crtCommand.getUsername());
        //  Set start time
        selectedPlaylist.setStartTime(crtCommand.getTimestamp());
        //  Set remaining time
        selectedPlaylist.setRemainingTime(selectedPlaylist.getPlaylist().getDuration());

        return selectedPlaylist;
    }

    public static PodcastSelection getPodcastSelection(Command crtCommand, LibraryInput library, LastSelection lastSelection) {
        PodcastSelection selectedPodcast = new PodcastSelection();
        //  Set name
        for (PodcastInput podcast : library.getPodcasts()) {
            if (podcast.getName().equals(lastSelection.getSelectionName())) {
                selectedPodcast.setPodcast(podcast);
                break;
            }
        }
        //  Set user
        selectedPodcast.setUser(crtCommand.getUsername());
        //  Set start time
        selectedPodcast.setStartTime(crtCommand.getTimestamp());
        //  Set remaining time
        selectedPodcast.setRemainingTime(selectedPodcast.getPodcast().getDuration());

        return selectedPodcast;
    }

    public static void storeResultForSelect(ArrayList<String> lastSearchResult, LastSelection lastSelection,
                                            ArrayList<String> names, String type) {
        //  First element specifies the type of items searched
        //  But first we need to clear the old search
        lastSearchResult.clear();
        lastSelection.resetSelection();
        if (!names.isEmpty()) {
            lastSearchResult.add(type);
            lastSearchResult.addAll(names);
        }
    }

    public static ObjectNode getStats(ItemSelection reqItem, ObjectMapper objectMapper, Command crtCommand) {
        ObjectNode stats = objectMapper.createObjectNode();

        //  Downsize item for JSON details
        if (reqItem instanceof SongSelection) {
            SongInput songItem = ((SongSelection) reqItem).getSong();

            //  Check remaining time
            int remainingTime = reqItem.getRemainingTime();

            //  If the song is playing we update the time
            if (!reqItem.isPaused()) {
                remainingTime = reqItem.getRemainingTime() - (crtCommand.getTimestamp() - reqItem.getStartTime());
                if (remainingTime < 0) {
                    remainingTime = 0;
                    reqItem.setPaused(true);
                }
                //  Replace start time with timestamp of update
                reqItem.setStartTime(crtCommand.getTimestamp());
                reqItem.setRemainingTime(remainingTime);
            }

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
            Playlist playlistItem = ((PlaylistSelection) reqItem).getPlaylist();

            //  If the podcast is playing we update the time
            if (!reqItem.isPaused()) {
                reqItem.updateRemainingTime(crtCommand.getTimestamp());
            }

            //  Check remaining time
            int remainingTime = reqItem.getRemainingTime();

            if (remainingTime == 0) {
                //  Set name
                stats.put("name", "");

                //  Set remaining time
                stats.put("remainedTime", 0);
            } else {
                //  We find the current episode
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
            PodcastInput podcastItem = ((PodcastSelection) reqItem).getPodcast();

            //  If the podcast is playing we update the time
            if (!reqItem.isPaused()) {
                reqItem.updateRemainingTime(crtCommand.getTimestamp());
            }

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

                int duration = ((PodcastSelection) reqItem).getPodcast().getDuration();

                for (EpisodeInput episode : ((PodcastSelection) reqItem).getPodcast().getEpisodes()) {
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

        return null;
    }

    public static String getAddRemoveMessage(ArrayList<ItemSelection> player, ArrayList<Playlist> playlists,
                                             ArrayList<UserPlaylists> usersPlaylists, Command crtCommand) {
        String message = "";

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

        } else if (isSong == 0){
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

    public static String getLikeMessage(ArrayList<ItemSelection> player, ArrayList<UserPlaylists> usersPlaylists, Command crtCommand) {
        String message = "";

        //  We begin by checking if there is a loaded source
        //  The source MUST be a song
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
                    user.getLikedSongs().add(crtSong);
                }

                //  Lastly, the message is set
                if (found == 0) {
                    message = "Like registered successfully.";
                } else {
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
                    user.getLikedSongs().add(crtSongInPlaylist);
                }

                //  Lastly, the message is set
                if (found == 0) {
                    message = "Like registered successfully.";
                } else {
                    message = "Unlike registered successfully.";
                }
            } else {
                message = "ERROR. User not found";
            }

        }

        return message;
    }
}

