package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.*;
import main.CreatorClasses.ArtistClasses.Event;
import main.CreatorClasses.ArtistClasses.Management;
import main.CommandHelper.Command;
import main.CreatorClasses.HostClasses.Announcement;
import main.CreatorClasses.HostClasses.HostInfo;
import main.PagingClasses.Page;
import main.PlaylistClasses.Album;
import main.PlaylistClasses.Playlist;
import main.PlaylistClasses.UserPlaylists;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.PodcastSelection;
import main.SongClasses.SongLikes;
import main.UtilityClasses.Constants;

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
                            podcasts, objectMapper, library, lastSearchResult,
                            steps, playlists, albums);

                    outputs.add(searchOutput);
                }

                case "select" -> {
                    ObjectNode selectOutput;
                    selectOutput = doSelect(objectMapper, crtCommand,
                            lastSearchResult, steps, library,
                            pageSystem, usersPlaylists, managements,
                            hostInfos);

                    outputs.add(selectOutput);
                }

                case "load" -> {
                    ObjectNode loadOutput;
                    loadOutput = doLoad(objectMapper, crtCommand,
                            steps, lastSearchResult, library, player,
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
                            crtCommand, player, playlists, library);

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
                            steps, lastSearchResult, playlists,
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
                            podcasts);

                    outputs.add(deleteUserOutput);
                }

                case "addPodcast" -> {
                    ObjectNode addPodcastOutput = objectMapper.createObjectNode();

                    addPodcastOutput.put("command", "addPodcast");
                    addPodcastOutput.put("user", crtCommand.getUsername());
                    addPodcastOutput.put("timestamp", crtCommand.getTimestamp());

                    String message;

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

                    addPodcastOutput.put("message", message);

                    outputs.add(addPodcastOutput);
                }

                case "addAnnouncement" -> {
                    ObjectNode addAnnouncementOutput = objectMapper.createObjectNode();

                    addAnnouncementOutput.put("command", "addAnnouncement");
                    addAnnouncementOutput.put("user", crtCommand.getUsername());
                    addAnnouncementOutput.put("timestamp", crtCommand.getTimestamp());

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

                    addAnnouncementOutput.put("message", message);
                    outputs.add(addAnnouncementOutput);
                }

                case "removeAnnouncement" -> {
                    ObjectNode removeAnnouncementOutput = objectMapper.createObjectNode();

                    removeAnnouncementOutput.put("command", "removeAnnouncement");
                    removeAnnouncementOutput.put("user", crtCommand.getUsername());
                    removeAnnouncementOutput.put("timestamp", crtCommand.getTimestamp());

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

                    removeAnnouncementOutput.put("message", message);
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


}

