package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import main.CommandHelper.Command;
import main.CommandHelper.Search;
import main.CreatorClasses.ArtistClasses.Management;
import main.CreatorClasses.HostClasses.HostInfo;
import main.PagingClasses.Page;
import main.PlaylistClasses.Album;
import main.PlaylistClasses.Playlist;
import main.PlaylistClasses.UserPlaylists;
import main.SelectionClasses.ItemSelection;
import main.SelectionClasses.PodcastSelection;
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
                            library, usersPlaylists, albums, songsLikes);

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
                    ObjectNode showPodcastsOutput;
                    showPodcastsOutput = doShowPodcasts(objectMapper,
                            crtCommand, usersPlaylists);

                    outputs.add(showPodcastsOutput);
                }

                case "removeAlbum" -> {
                    ObjectNode removeAlbumOutput;
                    removeAlbumOutput = doRemoveAlbum(objectMapper, crtCommand,
                            library, usersPlaylists, player, playlists,
                            songsLikes, albums);

                    outputs.add(removeAlbumOutput);
                }

                case "changePage" -> {
                    ObjectNode changePageOutput;
                    changePageOutput = doChangePage(objectMapper, crtCommand,
                            pageSystem, usersPlaylists);

                    outputs.add(changePageOutput);
                }

                case "removePodcast" -> {
                    ObjectNode removePodcastOutput;
                    removePodcastOutput = doRemovePodcast(objectMapper,
                            crtCommand, usersPlaylists, player,
                            library, podcasts);

                    outputs.add(removePodcastOutput);
                }

                case "removeEvent" -> {
                    ObjectNode removeEventOutput;
                    removeEventOutput = doRemoveEvent(objectMapper,
                            crtCommand, library, managements);

                    outputs.add(removeEventOutput);
                }

                case "getTop5Albums" -> {
                    ObjectNode topAlbumsOutput;
                    topAlbumsOutput = doGetTop5Albums(objectMapper,
                            crtCommand, albums);

                    outputs.add(topAlbumsOutput);
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

