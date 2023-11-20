package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;

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
        ArrayList<ItemSelection> crtSelections = new ArrayList<>();

        //  Keeping last selection for loading
        LastSelection lastSelection = new LastSelection();

        //  Creating an array list of playlists
        ArrayList<Playlist> playlists = new ArrayList<>();

        //  IMPORTANT VARIABLES DECLARATION ENDS HERE

        //  Parsing commands
        for (Command crtCommand : commands) {
            switch(crtCommand.getCommand()) {
                //  SEARCH COMMAND
                case "search" -> {
                    //  Searching for a song
                    ObjectNode searchOutput = objectMapper.createObjectNode();
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

                            //  Setting the output
                            searchOutput.put("command", "search");
                            searchOutput.put("user", crtCommand.getUsername());
                            searchOutput.put("timestamp", crtCommand.getTimestamp());
                            searchOutput.put("message", "Search returned " + result.size() + " results");

                            //  Extracting the names of the songs
                            String[] songNames = new String[result.size()];
                            for (int songIndex = 0; songIndex < result.size(); songIndex++) {
                                songNames[songIndex] = result.get(songIndex).getName();
                            }
                            searchOutput.put("results", Arrays.toString(songNames));

                            //  Storing the result in case we need to select it later
                            //  First element specifies the type of items searched
                            //  But first we need to clear the old search
                            lastSearchResult.clear();
                            lastSelection.resetSelection();
                            lastSearchResult.add("song");
                            lastSearchResult.addAll(List.of(songNames));
                            initLastSearchResult = true;

                            //  Adding the output in JSON Array
                            outputs.add(searchOutput);
                        }

                        case "playlist" -> {
                            Filters filters = crtCommand.getFilters();
                            ArrayList<Playlist> result = new ArrayList<>();

                            //  Found playlists will be added in result array
                            searchForPlaylists(filters, result, playlists);

                            //  Taking out private playlists
                            playlists.removeIf(playlist -> !playlist.getOwner().equals(crtCommand.getUsername())
                                    && !playlist.isVisibility());

                            //  Truncate results if needed
                            if (result.size() > 5) {
                                result.subList(5, result.size()).clear();
                            }

                            //  Setting the output
                            searchOutput.put("command", "search");
                            searchOutput.put("user", crtCommand.getUsername());
                            searchOutput.put("timestamp", crtCommand.getTimestamp());
                            searchOutput.put("message", "Search returned " + result.size() + " results");

                            //  Extracting the names of the playlists
                            String[] playlistNames = new String[result.size()];
                            for (int pIndex = 0; pIndex < result.size(); pIndex++) {
                                playlistNames[pIndex] = result.get(pIndex).getName();
                            }
                            searchOutput.put("results", Arrays.toString(playlistNames));

                            //  Storing the result in case we need to select it later
                            //  First element specifies the type of items searched
                            //  But first we need to clear the old search
                            lastSearchResult.clear();
                            lastSelection.resetSelection();
                            lastSearchResult.add("playlist");
                            lastSearchResult.addAll(List.of(playlistNames));
                            initLastSearchResult = true;

                            //  Adding the output in JSON Array
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

                            //  Setting the output
                            searchOutput.put("command", "search");
                            searchOutput.put("user", crtCommand.getUsername());
                            searchOutput.put("timestamp", crtCommand.getTimestamp());
                            searchOutput.put("message", "Search returned " + result.size() + " results");

                            //  Extracting the names of the playlists
                            String[] podcastNames = new String[result.size()];
                            for (int pIndex = 0; pIndex < result.size(); pIndex++) {
                                podcastNames[pIndex] = result.get(pIndex).getName();
                            }
                            searchOutput.put("results", Arrays.toString(podcastNames));

                            //  Storing the result in case we need to select it later
                            //  First element specifies the type of items searched
                            //  But first we need to clear the old search
                            lastSearchResult.clear();
                            lastSelection.resetSelection();
                            lastSearchResult.add("podcast");
                            lastSearchResult.addAll(List.of(podcastNames));
                            initLastSearchResult = true;

                            //  Adding the output in JSON Array
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
                    String message = getMessage(lastSearchResult, crtCommand);
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

                            //  Add selection to array
                            crtSelections.add(selectedSong);
                        }

                        //  Loading the playlist into the database
                        if (lastSelection.getSelectionType().equals("playlist")) {
                            PlaylistSelection selectedPlaylist = getPlaylistSelection(crtCommand, playlists, lastSelection);

                            //  Add selection to array
                            crtSelections.add(selectedPlaylist);
                        }

                        //  Loading the podcast into the database
                        if (lastSelection.getSelectionType().equals("podcast")) {
                            PodcastSelection selectedPodcast = getPodcastSelection(crtCommand, library, lastSelection);

                            //  Add selection to array
                            crtSelections.add(selectedPodcast);
                        }
                    }

                    outputs.add(loadOutput);
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

    public static String getMessage(ArrayList<String> lastSearchResult, Command crtCommand) {
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
        //  Set name
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
        return selectedPodcast;
    }
}

