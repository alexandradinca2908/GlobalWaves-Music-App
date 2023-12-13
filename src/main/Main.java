package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.*;
import main.CommandHelper.Search;
import main.CreatorClasses.ArtistClasses.Management;
import main.CommandHelper.Command;
import main.CreatorClasses.HostClasses.Announcement;
import main.CreatorClasses.HostClasses.HostInfo;
import main.PagingClasses.Page;
import main.PlaylistClasses.Album;
import main.PlaylistClasses.Playlist;
import main.PlaylistClasses.UserPlaylists;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.Playlists.AlbumSelection;
import main.SelectionClasses.PodcastSelection;
import main.SelectionClasses.SongSelection;
import main.SongClasses.SongLikes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import static main.UtilityClasses.DoCommands.*;

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

        //  Storing all searches in an array
        ArrayList<Search> searches = new ArrayList<>();

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

        //  Creating an array list of all the albums
        ArrayList<Album> albums = new ArrayList<>();

        //  Initializing Page System
        ArrayList<Page> pageSystem = new ArrayList<>();

        for (UserPlaylists user : usersPlaylists) {
            Page crtPage = new Page();
            crtPage.setPageOwner(user.getUser());
            crtPage.setUserPlaylists(user);

            pageSystem.add(crtPage);
        }

        //  Creating an array for artist management
        ArrayList<Management> managements = new ArrayList<>();

        for (UserInput user : library.getUsers()) {
            if (user.getType().equals("artist")) {
                Management crtManagement = new Management();
                crtManagement.setArtist(user);

                managements.add(crtManagement);
            }
        }

        //  Creating an array for host management
        ArrayList<HostInfo> hostInfos = new ArrayList<>();

        for (UserInput user : library.getUsers()) {
            if (user.getType().equals("host")) {
                HostInfo crtHostInfo = new HostInfo();
                crtHostInfo.setHost(user);

                hostInfos.add(crtHostInfo);
            }
        }

        //  IMPORTANT VARIABLES DECLARATION ENDS HERE

        //  Parsing commands
        for (Command crtCommand : commands) {
            switch (crtCommand.getCommand()) {
                case "search" -> {
                    ObjectNode searchOutput;
                    searchOutput = doSearch(player, crtCommand,
                            podcasts, objectMapper, library, searches,
                            playlists, albums);

                    outputs.add(searchOutput);
                }

                case "select" -> {
                    ObjectNode selectOutput;
                    selectOutput = doSelect(objectMapper, crtCommand,
                            searches, library,
                            pageSystem, usersPlaylists, managements,
                            hostInfos);

                    outputs.add(selectOutput);
                }

                case "load" -> {
                    ObjectNode loadOutput;
                    loadOutput = doLoad(objectMapper, crtCommand,
                            searches, library, player,
                            playlists, podcasts, albums);

                    outputs.add(loadOutput);
                }

                case "status" -> {
                    ObjectNode statusOutput;
                    statusOutput = doStatus(objectMapper, crtCommand, player,
                            podcasts, library);

                    outputs.add(statusOutput);
                }

                case "playPause" -> {
                    ObjectNode playPauseOutput;
                    playPauseOutput = doPlayPause(objectMapper, crtCommand, player,
                            podcasts, library);

                    outputs.add(playPauseOutput);
                }

                case "createPlaylist" -> {
                    ObjectNode createPlaylistOutput;
                    createPlaylistOutput = doCreatePlaylist(objectMapper,
                            crtCommand, playlists, usersPlaylists,
                            library);

                    outputs.add(createPlaylistOutput);
                }

                case "addRemoveInPlaylist" -> {
                    ObjectNode addRemoveOutput;
                    addRemoveOutput = doAddRemoveInPlaylist(objectMapper,
                            crtCommand, player, playlists, library,
                            podcasts);

                    outputs.add(addRemoveOutput);
                }

                case "like" -> {
                    ObjectNode likeOutput;
                    likeOutput = doLike(objectMapper, crtCommand, player,
                            usersPlaylists, songsLikes, library);

                    outputs.add(likeOutput);
                }

                case "showPlaylists" -> {
                    ObjectNode showPlaylistsOutput;
                    showPlaylistsOutput = doShowPlaylists(objectMapper,
                            crtCommand, usersPlaylists);

                    outputs.add(showPlaylistsOutput);
                }

                case "showPreferredSongs" -> {
                    ObjectNode showPreferredSongsOutput;
                    showPreferredSongsOutput = doShowPreferredSongs(objectMapper,
                            crtCommand, usersPlaylists);

                    outputs.add(showPreferredSongsOutput);
                }

                case "repeat" -> {
                    ObjectNode repeatOutput;
                    repeatOutput = doRepeat(objectMapper, crtCommand,
                            player, podcasts, library);

                    outputs.add(repeatOutput);
                }

                case "shuffle" -> {
                    ObjectNode shuffleOutput;
                    shuffleOutput = doShuffle(objectMapper, crtCommand,
                            player, podcasts, library);

                    outputs.add(shuffleOutput);
                }

                case "forward" -> {
                    ObjectNode forwardOutput;
                    forwardOutput = doForward(objectMapper, crtCommand,
                            podcasts, player, library);

                    outputs.add(forwardOutput);
                }

                case "backward" -> {
                    ObjectNode backwardOutput;
                    backwardOutput = doBackward(objectMapper, crtCommand,
                            podcasts, player, library);

                    outputs.add(backwardOutput);
                }

                case "next" -> {
                    ObjectNode nextOutput;
                    nextOutput = doNext(objectMapper, crtCommand,
                            podcasts, player, library);

                    outputs.add(nextOutput);
                }

                case "prev" -> {
                    ObjectNode prevOutput;
                    prevOutput = doPrev(objectMapper, crtCommand,
                            podcasts, player, library);

                    outputs.add(prevOutput);
                }

                case "follow" -> {
                    ObjectNode followOutput;
                    followOutput = doFollow(objectMapper, crtCommand,
                            searches, playlists,
                            usersPlaylists, library);

                    outputs.add(followOutput);
                }

                case "switchVisibility" -> {
                    ObjectNode switchOutput;
                    switchOutput = doSwitchVisibility(objectMapper, crtCommand,
                            usersPlaylists, library);

                    outputs.add(switchOutput);
                }

                case "getTop5Songs" -> {
                    ObjectNode topSongsOutput;
                    topSongsOutput = doGetTop5Songs(objectMapper, crtCommand,
                            songsLikes);

                    outputs.add(topSongsOutput);
                }

                case "getTop5Playlists" -> {
                    ObjectNode topPlaylistsOutput;
                    topPlaylistsOutput = doGetTop5Playlists(objectMapper,
                            crtCommand, playlists);

                    outputs.add(topPlaylistsOutput);
                }

                case "switchConnectionStatus" -> {
                    ObjectNode switchConnectionOutput;
                    switchConnectionOutput = doSwitchConnectionStatus(objectMapper,
                            crtCommand, player, library, podcasts);

                    outputs.add(switchConnectionOutput);
                }

                case "getOnlineUsers" -> {
                    ObjectNode getUsersOutput;
                    getUsersOutput = doGetOnlineUsers(objectMapper,
                            crtCommand, library);

                    outputs.add(getUsersOutput);
                }

                case "addUser" -> {
                    ObjectNode addUserOutput;
                    addUserOutput = doAddUser(objectMapper,
                            crtCommand, library, usersPlaylists,
                            pageSystem, managements, hostInfos);

                    outputs.add(addUserOutput);
                }

                case "addAlbum" -> {
                    ObjectNode addUserOutput;
                    addUserOutput = doAddAlbum(objectMapper, crtCommand,
                            library, usersPlaylists, albums);

                    outputs.add(addUserOutput);
                }

                case "showAlbums" -> {
                    ObjectNode showAlbumsOutput;
                    showAlbumsOutput = doShowAlbums(objectMapper,
                            crtCommand, usersPlaylists);

                    outputs.add(showAlbumsOutput);
                }

                case "printCurrentPage" -> {
                    ObjectNode printCurrentPageOutput;
                    printCurrentPageOutput = doPrintCurrentPage(objectMapper,
                            crtCommand, pageSystem, library);

                    outputs.add(printCurrentPageOutput);
                }

                case "addEvent" -> {
                    ObjectNode addEventOutput;
                    addEventOutput = doAddEvent(objectMapper, library,
                            crtCommand, managements);

                    outputs.add(addEventOutput);
                }

                case "addMerch" -> {
                    ObjectNode addMerchOutput;
                    addMerchOutput = doAddMerch(objectMapper, library,
                            crtCommand, managements);

                    outputs.add(addMerchOutput);
                }

                case "getAllUsers" -> {
                    ObjectNode getAllUsersOutput;
                    getAllUsersOutput = doGetAllUsers(objectMapper, library, crtCommand);

                    outputs.add(getAllUsersOutput);
                }

                case "deleteUser" -> {
                    ObjectNode deleteUserOutput;
                    deleteUserOutput = doDeleteUser(objectMapper, library, crtCommand,
                            player, playlists, usersPlaylists, albums, songsLikes,
                            podcasts, pageSystem);

                    outputs.add(deleteUserOutput);
                }

                case "addPodcast" -> {
                    ObjectNode addPodcastOutput;
                    addPodcastOutput =  doAddPodcast(objectMapper, crtCommand,
                            library, usersPlaylists);

                    outputs.add(addPodcastOutput);
                }

                case "addAnnouncement" -> {
                    ObjectNode addAnnouncementOutput;
                    addAnnouncementOutput = doAddAnnouncement(objectMapper,
                            crtCommand, library, hostInfos);

                    outputs.add(addAnnouncementOutput);
                }

                case "removeAnnouncement" -> {
                    ObjectNode removeAnnouncementOutput;
                    removeAnnouncementOutput = doRemoveAnnouncement(objectMapper,
                            crtCommand, library, hostInfos);

                    outputs.add(removeAnnouncementOutput);
                }

                case "showPodcasts" -> {
                    ObjectNode showPodcastsOutput = objectMapper.createObjectNode();

                    showPodcastsOutput.put("command", "showPodcasts");
                    showPodcastsOutput.put("user", crtCommand.getUsername());
                    showPodcastsOutput.put("timestamp", crtCommand.getTimestamp());

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
                        for (PodcastInput podcast : crtUser.getPodcasts()) {
                            ObjectNode resultNode = objectMapper.createObjectNode();

                            //  Set album data
                            resultNode.put("name", podcast.getName());

                            ArrayList<String> episodeNames = new ArrayList<>();
                            ArrayList<EpisodeInput> episodes = podcast.getEpisodes();

                            for (EpisodeInput episode : episodes) {
                                episodeNames.add(episode.getName());
                            }
                            resultNode.putPOJO("episodes", episodeNames);

                            result.add(resultNode);
                        }
                    }

                    showPodcastsOutput.putPOJO("result", result);
                    outputs.add(showPodcastsOutput);
                }

                case "removeAlbum" -> {
                    ObjectNode removeAlbumOutput = objectMapper.createObjectNode();

                    removeAlbumOutput.put("command", "removeAlbum");
                    removeAlbumOutput.put("user", crtCommand.getUsername());
                    removeAlbumOutput.put("timestamp", crtCommand.getTimestamp());

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

                    removeAlbumOutput.put("message", message);
                    outputs.add(removeAlbumOutput);
                }

                case "changePage" -> {
                    ObjectNode changePageOutput = objectMapper.createObjectNode();

                    changePageOutput.put("command", "changePage");
                    changePageOutput.put("user", crtCommand.getUsername());
                    changePageOutput.put("timestamp", crtCommand.getTimestamp());

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

                    changePageOutput.put("message", message);
                    outputs.add(changePageOutput);
                }

                case "removePodcast" -> {
                    ObjectNode removePodcastOutput = objectMapper.createObjectNode();

                    removePodcastOutput.put("command", "removePodcast");
                    removePodcastOutput.put("user", crtCommand.getUsername());
                    removePodcastOutput.put("timestamp", crtCommand.getTimestamp());

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

                    removePodcastOutput.put("message", message);
                    outputs.add(removePodcastOutput);
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
                                    final ArrayList<PodcastSelection> podcasts,
                                    final LibraryInput library) {
        //  Iterate through the player and update times
        //  Remove all finished sources
        ArrayList<ItemSelection> removableItems = new ArrayList<>();

        //  Username converted to user
        UserInput playerUser = null;

        for (ItemSelection item : player) {
            for (UserInput user : library.getUsers()) {
                if (user.getUsername().equals(item.getUser())) {
                    playerUser = user;
                    break;
                }
            }
            //  Only update time of online players
            if (playerUser != null && playerUser.isOnline()) {
                item.updateRemainingTime(crtCommand.getTimestamp());

                if (item.getRemainingTime() == 0) {
                    if (item instanceof PodcastSelection) {
                        podcasts.remove(item);
                    }
                    removableItems.add(item);
                }
            }
        }

        for (ItemSelection item : removableItems) {
            player.remove(item);
        }

        removableItems.clear();
    }

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

    public static ObjectNode doAddPodcast(final ObjectMapper objectMapper,
                                          final Command crtCommand,
                                          final LibraryInput library,
                                          final ArrayList<UserPlaylists> usersPlaylists) {
        ObjectNode addPodcastOutput = objectMapper.createObjectNode();

        addPodcastOutput.put("command", "addPodcast");
        addPodcastOutput.put("user", crtCommand.getUsername());
        addPodcastOutput.put("timestamp", crtCommand.getTimestamp());

        String message = getAddPodcastMessage(library, crtCommand,
                usersPlaylists);
        addPodcastOutput.put("message", message);

        return addPodcastOutput;
    }

    public static ObjectNode doAddAnnouncement(final ObjectMapper objectMapper,
                                               final Command crtCommand,
                                               final LibraryInput library,
                                               final ArrayList<HostInfo> hostInfos) {
        ObjectNode addAnnouncementOutput = objectMapper.createObjectNode();

        addAnnouncementOutput.put("command", "addAnnouncement");
        addAnnouncementOutput.put("user", crtCommand.getUsername());
        addAnnouncementOutput.put("timestamp", crtCommand.getTimestamp());

        String message = getAddAnnouncementMessage(library,
                crtCommand, hostInfos);
        addAnnouncementOutput.put("message", message);

        return addAnnouncementOutput;
    }

    public static ObjectNode doRemoveAnnouncement(final ObjectMapper objectMapper,
                                                  final Command crtCommand,
                                                  final LibraryInput library,
                                                  final ArrayList<HostInfo> hostInfos) {
        ObjectNode removeAnnouncementOutput = objectMapper.createObjectNode();

        removeAnnouncementOutput.put("command", "removeAnnouncement");
        removeAnnouncementOutput.put("user", crtCommand.getUsername());
        removeAnnouncementOutput.put("timestamp", crtCommand.getTimestamp());

        String message = getRemoveAnnouncementMessage(library,
                crtCommand, hostInfos);

        removeAnnouncementOutput.put("message", message);

        return removeAnnouncementOutput;
    }
}

