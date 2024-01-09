package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
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
import main.LikeClasses.SongLikes;
import main.UtilityClasses.Constants;
import main.WrappedDatabase.AllUserStats.ArtistStatistics;
import main.WrappedDatabase.AllUserStats.UserStatistics;
import main.WrappedDatabase.Statistics;
import main.WrappedDatabase.StatsFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static main.UtilityClasses.Do.DoCommands.doSearch;
import static main.UtilityClasses.Do.DoCommands.doSelect;
import static main.UtilityClasses.Do.DoCommands.doLoad;
import static main.UtilityClasses.Do.DoCommands.doGetTop5Albums;
import static main.UtilityClasses.Do.DoCommands.doGetTop5Artists;
import static main.UtilityClasses.Do.DoCommands.doStatus;
import static main.UtilityClasses.Do.DoCommands.doShuffle;
import static main.UtilityClasses.Do.DoCommands.doShowPreferredSongs;
import static main.UtilityClasses.Do.DoCommands.doShowPodcasts;
import static main.UtilityClasses.Do.DoCommands.doShowPlaylists;
import static main.UtilityClasses.Do.DoCommands.doShowAlbums;
import static main.UtilityClasses.Do.DoCommands.doRepeat;
import static main.UtilityClasses.Do.DoCommands.doPrintCurrentPage;
import static main.UtilityClasses.Do.DoCommands.doPlayPause;
import static main.UtilityClasses.Do.DoCommands.doGetTop5Songs;
import static main.UtilityClasses.Do.DoCommands.doGetTop5Playlists;
import static main.UtilityClasses.Do.DoCommands.doGetOnlineUsers;
import static main.UtilityClasses.Do.DoCommands.doGetAllUsers;
import static main.UtilityClasses.Do.DoCommands.doFollow;
import static main.UtilityClasses.Do.DoCommands.doCreatePlaylist;

import static main.UtilityClasses.Do.DoCommandsMessage.doAddAlbum;
import static main.UtilityClasses.Do.DoCommandsMessage.doAddAnnouncement;
import static main.UtilityClasses.Do.DoCommandsMessage.doAddEvent;
import static main.UtilityClasses.Do.DoCommandsMessage.doAddMerch;
import static main.UtilityClasses.Do.DoCommandsMessage.doAddPodcast;
import static main.UtilityClasses.Do.DoCommandsMessage.doAddRemoveInPlaylist;
import static main.UtilityClasses.Do.DoCommandsMessage.doSwitchVisibility;
import static main.UtilityClasses.Do.DoCommandsMessage.doSwitchConnectionStatus;
import static main.UtilityClasses.Do.DoCommandsMessage.doRemovePodcast;
import static main.UtilityClasses.Do.DoCommandsMessage.doRemoveEvent;
import static main.UtilityClasses.Do.DoCommandsMessage.doRemoveAnnouncement;
import static main.UtilityClasses.Do.DoCommandsMessage.doRemoveAlbum;
import static main.UtilityClasses.Do.DoCommandsMessage.doPrev;
import static main.UtilityClasses.Do.DoCommandsMessage.doNext;
import static main.UtilityClasses.Do.DoCommandsMessage.doLike;
import static main.UtilityClasses.Do.DoCommandsMessage.doForward;
import static main.UtilityClasses.Do.DoCommandsMessage.doDeleteUser;
import static main.UtilityClasses.Do.DoCommandsMessage.doChangePage;
import static main.UtilityClasses.Do.DoCommandsMessage.doBackward;
import static main.UtilityClasses.Do.DoCommandsMessage.doAddUser;

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

        //  Populating the wrapped statistics database
        for (UserInput user : library.getUsers()) {
            StatsFactory.createStats(user);
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
                            podcasts, library, albums);

                    outputs.add(statusOutput);
                }

                case "playPause" -> {
                    ObjectNode playPauseOutput;
                    playPauseOutput = doPlayPause(objectMapper, crtCommand, player,
                            podcasts, library, albums);

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
                            podcasts, albums);

                    outputs.add(addRemoveOutput);
                }

                case "like" -> {
                    ObjectNode likeOutput;
                    likeOutput = doLike(objectMapper, crtCommand, player,
                            usersPlaylists, songsLikes, library,
                            albums);

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
                            player, podcasts, library, albums);

                    outputs.add(repeatOutput);
                }

                case "shuffle" -> {
                    ObjectNode shuffleOutput;
                    shuffleOutput = doShuffle(objectMapper, crtCommand,
                            player, podcasts, library, albums);

                    outputs.add(shuffleOutput);
                }

                case "forward" -> {
                    ObjectNode forwardOutput;
                    forwardOutput = doForward(objectMapper, crtCommand,
                            podcasts, player, library, albums);

                    outputs.add(forwardOutput);
                }

                case "backward" -> {
                    ObjectNode backwardOutput;
                    backwardOutput = doBackward(objectMapper, crtCommand,
                            podcasts, player, library, albums);

                    outputs.add(backwardOutput);
                }

                case "next" -> {
                    ObjectNode nextOutput;
                    nextOutput = doNext(objectMapper, crtCommand,
                            podcasts, player, library, albums);

                    outputs.add(nextOutput);
                }

                case "prev" -> {
                    ObjectNode prevOutput;
                    prevOutput = doPrev(objectMapper, crtCommand,
                            podcasts, player, library, albums);

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
                            crtCommand, player, library, podcasts, albums);

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
                            crtCommand, pageSystem, library, songsLikes);

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

                case "getTop5Artists" -> {
                    ObjectNode topArtistsOutput;
                    topArtistsOutput = doGetTop5Artists(objectMapper,
                            crtCommand, usersPlaylists);

                    outputs.add(topArtistsOutput);
                }

                case "wrapped" -> {
                    ObjectNode wrappedOutput = objectMapper.createObjectNode();;

                    wrappedOutput.put("command", "wrapped");
                    wrappedOutput.put("user", crtCommand.getUsername());
                    wrappedOutput.put("timestamp", crtCommand.getTimestamp());

                    ObjectNode result = objectMapper.createObjectNode();

                    //  Update the player
                    updatePlayer(player, crtCommand,
                            podcasts, library, albums);

                    //  Finding the user that queried the database
                    //  Filtering by user type
                    UserInput crtUser = null;

                    for (UserInput user : library.getUsers()) {
                        if (user.getUsername().equals(crtCommand.getUsername())) {
                            crtUser = user;
                            break;
                        }
                    }

                    if (crtUser.getType().equals("user")) {
                        //  Get user stats
                        UserStatistics userStats = null;
                        for(UserStatistics user : Statistics
                                .getWrappedStats().getUsersStatistics()) {
                            if (user.getUser().equals(crtUser)) {
                                userStats = user;
                                break;
                            }
                        }
                        //  See if there is anything to display
                        if (userStats.getTopSongs().isEmpty()
                                && userStats.getTopAlbums().isEmpty()
                                && userStats.getTopEpisodes().isEmpty()
                                && userStats.getTopArtists().isEmpty()
                                && userStats.getTopGenres().isEmpty()) {

                            result.put("result", "No data to show for "
                                    + crtUser.getUsername() + ".");
                        } else {
                            //  Sort Artist HashMap data and display if available
                            if (!userStats.getTopArtists().isEmpty()) {
                                ArrayList<Map.Entry<String, Integer>> artists;
                                artists = new ArrayList<>(userStats.getTopArtists().entrySet());

                                //  Sort
                                artists.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().compareTo(a2.getKey());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });

                                if (artists.size() > 5) {
                                    artists.subList(Constants.MAX_SIZE_5, artists.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode artistInfo = objectMapper.createObjectNode();
                                for (Map.Entry<String, Integer> entry : artists) {
                                    artistInfo.put(entry.getKey(), entry.getValue());
                                }
                                result.putPOJO("topArtists", artistInfo);
                            } else {
                                result.putPOJO("topArtists", "{ }");
                            }

                            //  Sort Genre HashMap data and display if available
                            if (!userStats.getTopGenres().isEmpty()) {
                                ArrayList<Map.Entry<String, Integer>> genres;
                                genres = new ArrayList<>(userStats.getTopGenres().entrySet());

                                //  Sort
                                genres.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().compareTo(a2.getKey());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (genres.size() > 5) {
                                    genres.subList(Constants.MAX_SIZE_5, genres.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode genreInfo = objectMapper.createObjectNode();
                                for (Map.Entry<String, Integer> entry : genres) {
                                    genreInfo.put(entry.getKey(), entry.getValue());
                                }
                                result.putPOJO("topGenres", genreInfo);
                            } else {
                                result.putPOJO("topGenres", "{ }");
                            }

                            //  Sort Song HashMap data and display if available
                            if (!userStats.getTopSongs().isEmpty()) {
                                ArrayList<Map.Entry<SongInput, Integer>> songs;
                                songs = new ArrayList<>(userStats.getTopSongs().entrySet());

                                //  Sort
                                songs.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getName()
                                                .compareTo(a2.getKey().getName());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (songs.size() > 5) {
                                    songs.subList(Constants.MAX_SIZE_5, songs.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode songInfo = objectMapper.createObjectNode();
                                for (Map.Entry<SongInput, Integer> entry : songs) {
                                    songInfo.put(entry.getKey().getName(), entry.getValue());
                                }
                                result.putPOJO("topSongs", songInfo);
                            } else {
                                result.putPOJO("topSongs", "{ }");
                            }

                            //  Sort Album HashMap data and display if available
                            if (!userStats.getTopAlbums().isEmpty()) {
                                ArrayList<Map.Entry<Album, Integer>> topAlbums;
                                topAlbums = new ArrayList<>(userStats.getTopAlbums().entrySet());

                                //  Sort
                                topAlbums.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getName().compareTo(a2.getKey().getName());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (topAlbums.size() > 5) {
                                    topAlbums.subList(Constants.MAX_SIZE_5, topAlbums.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode albumInfo = objectMapper.createObjectNode();
                                for (Map.Entry<Album, Integer> entry : topAlbums) {
                                    albumInfo.put(entry.getKey().getName(), entry.getValue());
                                }
                                result.putPOJO("topAlbums", albumInfo);
                            } else {
                                result.putPOJO("topAlbums", "{ }");
                            }

                            //  Sort Episode HashMap data and display if available
                            if (!userStats.getTopEpisodes().isEmpty()) {
                                ArrayList<Map.Entry<EpisodeInput, Integer>> episodes;
                                episodes = new ArrayList<>(userStats.getTopEpisodes().entrySet());

                                //  Sort
                                episodes.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getName().compareTo(a2.getKey().getName());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (episodes.size() > 5) {
                                    episodes.subList(Constants.MAX_SIZE_5, episodes.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode epInfo = objectMapper.createObjectNode();
                                for (Map.Entry<EpisodeInput, Integer> entry : episodes) {
                                    epInfo.put(entry.getKey().getName(), entry.getValue());
                                }
                                result.putPOJO("topEpisodes", epInfo);
                            } else {
                                result.putPOJO("topEpisodes", "{ }");
                            }
                        }

                    } else if (crtUser.getType().equals("artist")) {
                        //  Get artist stats
                        ArtistStatistics artistStats = null;
                        for(ArtistStatistics artist : Statistics
                                .getWrappedStats().getArtistsStatistics()) {
                            if (artist.getArtist().equals(crtUser)) {
                                artistStats = artist;
                                break;
                            }
                        }
                        //  See if there is anything to display
                        if (artistStats.getTopSongs().isEmpty()
                                && artistStats.getTopAlbums().isEmpty()
                                && artistStats.getTopFans().isEmpty()
                                && artistStats.getListeners().isEmpty()) {

                            result.put("result", "No data to show for "
                                    + crtUser.getUsername() + ".");
                        } else {
                            //  Sort Album HashMap data and display if available
                            if (!artistStats.getTopAlbums().isEmpty()) {
                                ArrayList<Map.Entry<Album, Integer>> topAlbums;
                                topAlbums = new ArrayList<>(artistStats.getTopAlbums().entrySet());

                                //  Sort
                                topAlbums.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getName().compareTo(a2.getKey().getName());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (topAlbums.size() > 5) {
                                    topAlbums.subList(Constants.MAX_SIZE_5, topAlbums.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode albumInfo = objectMapper.createObjectNode();
                                for (Map.Entry<Album, Integer> entry : topAlbums) {
                                    albumInfo.put(entry.getKey().getName(), entry.getValue());
                                }
                                result.putPOJO("topAlbums", albumInfo);
                            } else {
                                result.putPOJO("topAlbums", "{ }");
                            }

                            //  Sort Song HashMap data and display if available
                            if (!artistStats.getTopSongs().isEmpty()) {
                                ArrayList<Map.Entry<SongInput, Integer>> songs;
                                songs = new ArrayList<>(artistStats.getTopSongs().entrySet());

                                //  Sort
                                songs.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getName().compareTo(a2.getKey().getName());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (songs.size() > 5) {
                                    songs.subList(Constants.MAX_SIZE_5, songs.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode songInfo = objectMapper.createObjectNode();
                                for (Map.Entry<SongInput, Integer> entry : songs) {
                                    songInfo.put(entry.getKey().getName(), entry.getValue());
                                }
                                result.putPOJO("topSongs", songInfo);
                            } else {
                                result.putPOJO("topSongs", "{ }");
                            }

                            //  Sort Fans HashMap data and display if available
                            if (!artistStats.getTopFans().isEmpty()) {
                                ArrayList<Map.Entry<UserInput, Integer>> fans;
                                fans = new ArrayList<>(artistStats.getTopFans().entrySet());

                                //  Sort
                                fans.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getUsername().compareTo(a2.getKey().getUsername());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (fans.size() > 5) {
                                    fans.subList(Constants.MAX_SIZE_5, fans.size()).clear();
                                }

                                //  Add info to result array
                                ArrayList<String> fanInfo = new ArrayList<>();
                                for (Map.Entry<UserInput, Integer> entry : fans) {
                                    fanInfo.add(entry.getKey().getUsername());
                                }
                                result.putPOJO("topFans", fanInfo);
                            } else {
                                result.putPOJO("topFans", "{ }");
                            }

                            //  Listeners
                            result.putPOJO("listeners", artistStats.getListeners().size());
                        }
                    }

                    wrappedOutput.putPOJO("result", result);
                    outputs.add(wrappedOutput);
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
                                    final LibraryInput library,
                                    final ArrayList<Album> albums) {
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
                item.updateRemainingTime(crtCommand.getTimestamp(),
                        albums);

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

