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
import main.commandhelper.Command;
import main.commandhelper.Search;
import main.creatorclasses.artistclasses.Management;
import main.creatorclasses.artistclasses.Merch;
import main.creatorclasses.hostclasses.HostInfo;
import main.creatorclasses.subscription.CreatorChannel;
import main.creatorclasses.subscription.NotificationBar;
import main.monetization.ArtistRevenue;
import main.monetization.PremiumUser;
import main.pagingclasses.Page;
import main.playlistclasses.Album;
import main.playlistclasses.Playlist;
import main.playlistclasses.UserData;
import main.selectionclasses.ItemSelection;
import main.selectionclasses.PodcastSelection;
import main.likeclasses.SongLikes;
import main.utilityclasses.Constants;
import main.wrappeddatabase.alluserstats.ArtistStatistics;
import main.wrappeddatabase.alluserstats.HostStatistics;
import main.wrappeddatabase.alluserstats.UserStatistics;
import main.wrappeddatabase.Statistics;
import main.wrappeddatabase.StatsFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static main.utilityclasses.doclasses.DoCommands.doSearch;
import static main.utilityclasses.doclasses.DoCommands.doSelect;
import static main.utilityclasses.doclasses.DoCommands.doLoad;
import static main.utilityclasses.doclasses.DoCommands.doGetTop5Albums;
import static main.utilityclasses.doclasses.DoCommands.doGetTop5Artists;
import static main.utilityclasses.doclasses.DoCommands.doStatus;
import static main.utilityclasses.doclasses.DoCommands.doShuffle;
import static main.utilityclasses.doclasses.DoCommands.doShowPreferredSongs;
import static main.utilityclasses.doclasses.DoCommands.doShowPodcasts;
import static main.utilityclasses.doclasses.DoCommands.doShowPlaylists;
import static main.utilityclasses.doclasses.DoCommands.doShowAlbums;
import static main.utilityclasses.doclasses.DoCommands.doRepeat;
import static main.utilityclasses.doclasses.DoCommands.doPrintCurrentPage;
import static main.utilityclasses.doclasses.DoCommands.doPlayPause;
import static main.utilityclasses.doclasses.DoCommands.doGetTop5Songs;
import static main.utilityclasses.doclasses.DoCommands.doGetTop5Playlists;
import static main.utilityclasses.doclasses.DoCommands.doGetOnlineUsers;
import static main.utilityclasses.doclasses.DoCommands.doGetAllUsers;
import static main.utilityclasses.doclasses.DoCommands.doFollow;
import static main.utilityclasses.doclasses.DoCommands.doCreatePlaylist;

import static main.utilityclasses.doclasses.DoCommandsMessage.doAddAlbum;
import static main.utilityclasses.doclasses.DoCommandsMessage.doAddAnnouncement;
import static main.utilityclasses.doclasses.DoCommandsMessage.doAddEvent;
import static main.utilityclasses.doclasses.DoCommandsMessage.doAddMerch;
import static main.utilityclasses.doclasses.DoCommandsMessage.doAddPodcast;
import static main.utilityclasses.doclasses.DoCommandsMessage.doAddRemoveInPlaylist;
import static main.utilityclasses.doclasses.DoCommandsMessage.doSwitchVisibility;
import static main.utilityclasses.doclasses.DoCommandsMessage.doSwitchConnectionStatus;
import static main.utilityclasses.doclasses.DoCommandsMessage.doRemovePodcast;
import static main.utilityclasses.doclasses.DoCommandsMessage.doRemoveEvent;
import static main.utilityclasses.doclasses.DoCommandsMessage.doRemoveAnnouncement;
import static main.utilityclasses.doclasses.DoCommandsMessage.doRemoveAlbum;
import static main.utilityclasses.doclasses.DoCommandsMessage.doPrev;
import static main.utilityclasses.doclasses.DoCommandsMessage.doNext;
import static main.utilityclasses.doclasses.DoCommandsMessage.doLike;
import static main.utilityclasses.doclasses.DoCommandsMessage.doForward;
import static main.utilityclasses.doclasses.DoCommandsMessage.doDeleteUser;
import static main.utilityclasses.doclasses.DoCommandsMessage.doChangePage;
import static main.utilityclasses.doclasses.DoCommandsMessage.doBackward;
import static main.utilityclasses.doclasses.DoCommandsMessage.doAddUser;

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
        ArrayList<UserData> usersData = new ArrayList<>();

        for (UserInput user : library.getUsers()) {
            UserData newUserPlaylists = new UserData();
            newUserPlaylists.setUser(user);

            usersData.add(newUserPlaylists);
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

        for (UserData user : usersData) {
            Page crtPage = new Page();
            crtPage.setPageOwner(user.getUser());
            crtPage.setUserData(user);

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

        //  Storing premium users
        ArrayList<PremiumUser> premiumUsers = new ArrayList<>();
        ArrayList<PremiumUser> cancelledPremiumUsers = new ArrayList<>();

        //  Creating channel-subscribers dynamic
        ArrayList<CreatorChannel> channels = new ArrayList<>();
        for (UserInput user : library.getUsers()) {
            CreatorChannel newCreator = new CreatorChannel();
            newCreator.setCreator(user);

            channels.add(newCreator);
        }

        ArrayList<NotificationBar> notificationBars = new ArrayList<>();
        for (UserInput user : library.getUsers()) {
            NotificationBar newBar = new NotificationBar();
            newBar.setSubscriber(user.getUsername());

            notificationBars.add(newBar);
        }

        //  Artist name who sold merch
        ArrayList<String> merchSellers = new ArrayList<>();

        //  IMPORTANT VARIABLES DECLARATION ENDS HERE
        System.out.println(filePathInput);
        //  Parsing commands
        for (Command crtCommand : commands) {
            switch (crtCommand.getCommand()) {
                case "search" -> {
                    ObjectNode searchOutput;
                    searchOutput = doSearch(player, crtCommand,
                            podcasts, objectMapper, library, searches,
                            playlists, albums, premiumUsers);

                    outputs.add(searchOutput);
                }

                case "select" -> {
                    ObjectNode selectOutput;
                    selectOutput = doSelect(objectMapper, crtCommand,
                            searches, library,
                            pageSystem, usersData, managements,
                            hostInfos);

                    outputs.add(selectOutput);
                }

                case "load" -> {
                    ObjectNode loadOutput;
                    loadOutput = doLoad(objectMapper, crtCommand,
                            searches, library, player,
                            playlists, podcasts, albums,
                            premiumUsers);

                    outputs.add(loadOutput);
                }

                case "status" -> {
                    ObjectNode statusOutput;
                    statusOutput = doStatus(objectMapper, crtCommand, player,
                            podcasts, library, albums, premiumUsers);

                    outputs.add(statusOutput);
                }

                case "playPause" -> {
                    ObjectNode playPauseOutput;
                    playPauseOutput = doPlayPause(objectMapper, crtCommand, player,
                            podcasts, library, albums, premiumUsers);

                    outputs.add(playPauseOutput);
                }

                case "createPlaylist" -> {
                    ObjectNode createPlaylistOutput;
                    createPlaylistOutput = doCreatePlaylist(objectMapper,
                            crtCommand, playlists, usersData,
                            library);

                    outputs.add(createPlaylistOutput);
                }

                case "addRemoveInPlaylist" -> {
                    ObjectNode addRemoveOutput;
                    addRemoveOutput = doAddRemoveInPlaylist(objectMapper,
                            crtCommand, player, playlists, library,
                            podcasts, albums, premiumUsers);

                    outputs.add(addRemoveOutput);
                }

                case "like" -> {
                    ObjectNode likeOutput;
                    likeOutput = doLike(objectMapper, crtCommand, player,
                            usersData, songsLikes, library,
                            albums, premiumUsers);

                    outputs.add(likeOutput);
                }

                case "showPlaylists" -> {
                    ObjectNode showPlaylistsOutput;
                    showPlaylistsOutput = doShowPlaylists(objectMapper,
                            crtCommand, usersData);

                    outputs.add(showPlaylistsOutput);
                }

                case "showPreferredSongs" -> {
                    ObjectNode showPreferredSongsOutput;
                    showPreferredSongsOutput = doShowPreferredSongs(objectMapper,
                            crtCommand, usersData);

                    outputs.add(showPreferredSongsOutput);
                }

                case "repeat" -> {
                    ObjectNode repeatOutput;
                    repeatOutput = doRepeat(objectMapper, crtCommand,
                            player, podcasts, library, albums, premiumUsers);

                    outputs.add(repeatOutput);
                }

                case "shuffle" -> {
                    ObjectNode shuffleOutput;
                    shuffleOutput = doShuffle(objectMapper, crtCommand,
                            player, podcasts, library, albums, premiumUsers);

                    outputs.add(shuffleOutput);
                }

                case "forward" -> {
                    ObjectNode forwardOutput;
                    forwardOutput = doForward(objectMapper, crtCommand,
                            podcasts, player, library, albums, premiumUsers);

                    outputs.add(forwardOutput);
                }

                case "backward" -> {
                    ObjectNode backwardOutput;
                    backwardOutput = doBackward(objectMapper, crtCommand,
                            podcasts, player, library, albums, premiumUsers);

                    outputs.add(backwardOutput);
                }

                case "next" -> {
                    ObjectNode nextOutput;
                    nextOutput = doNext(objectMapper, crtCommand,
                            podcasts, player, library, albums, premiumUsers);

                    outputs.add(nextOutput);
                }

                case "prev" -> {
                    ObjectNode prevOutput;
                    prevOutput = doPrev(objectMapper, crtCommand,
                            podcasts, player, library, albums, premiumUsers);

                    outputs.add(prevOutput);
                }

                case "follow" -> {
                    ObjectNode followOutput;
                    followOutput = doFollow(objectMapper, crtCommand,
                            searches, playlists,
                            usersData, library);

                    outputs.add(followOutput);
                }

                case "switchVisibility" -> {
                    ObjectNode switchOutput;
                    switchOutput = doSwitchVisibility(objectMapper, crtCommand,
                            usersData, library);

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
                            crtCommand, player, library, podcasts, albums, premiumUsers);

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
                            crtCommand, library, usersData,
                            pageSystem, managements, hostInfos,
                            channels, notificationBars);

                    outputs.add(addUserOutput);
                }

                case "addAlbum" -> {
                    ObjectNode addUserOutput;
                    addUserOutput = doAddAlbum(objectMapper, crtCommand,
                            library, usersData, albums, songsLikes,
                            channels, notificationBars);

                    outputs.add(addUserOutput);
                }

                case "showAlbums" -> {
                    ObjectNode showAlbumsOutput;
                    showAlbumsOutput = doShowAlbums(objectMapper,
                            crtCommand, usersData);

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
                            crtCommand, managements, channels, notificationBars);

                    outputs.add(addEventOutput);
                }

                case "addMerch" -> {
                    ObjectNode addMerchOutput;
                    addMerchOutput = doAddMerch(objectMapper, library,
                            crtCommand, managements, channels, notificationBars);

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
                            player, playlists, usersData, albums, songsLikes,
                            podcasts, pageSystem, premiumUsers);

                    outputs.add(deleteUserOutput);
                }

                case "addPodcast" -> {
                    ObjectNode addPodcastOutput;
                    addPodcastOutput =  doAddPodcast(objectMapper, crtCommand,
                            library, usersData, channels, notificationBars);

                    outputs.add(addPodcastOutput);
                }

                case "addAnnouncement" -> {
                    ObjectNode addAnnouncementOutput;
                    addAnnouncementOutput = doAddAnnouncement(objectMapper,
                            crtCommand, library, hostInfos, channels, notificationBars);

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
                            crtCommand, usersData);

                    outputs.add(showPodcastsOutput);
                }

                case "removeAlbum" -> {
                    ObjectNode removeAlbumOutput;
                    removeAlbumOutput = doRemoveAlbum(objectMapper, crtCommand,
                            library, usersData, player, playlists,
                            songsLikes, albums);

                    outputs.add(removeAlbumOutput);
                }

                case "changePage" -> {
                    ObjectNode changePageOutput;
                    changePageOutput = doChangePage(objectMapper, crtCommand,
                            pageSystem, usersData);

                    outputs.add(changePageOutput);
                }

                case "removePodcast" -> {
                    ObjectNode removePodcastOutput;
                    removePodcastOutput = doRemovePodcast(objectMapper,
                            crtCommand, usersData, player,
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
                            crtCommand, usersData);

                    outputs.add(topArtistsOutput);
                }

                case "wrapped" -> {
                    ObjectNode wrappedOutput = objectMapper.createObjectNode();

                    wrappedOutput.put("command", "wrapped");
                    wrappedOutput.put("user", crtCommand.getUsername());
                    wrappedOutput.put("timestamp", crtCommand.getTimestamp());

                    ObjectNode result = objectMapper.createObjectNode();

                    //  Update the player
                    updatePlayer(player, crtCommand,
                            podcasts, library, albums, premiumUsers);

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
                        for (UserStatistics user : Statistics
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

                            wrappedOutput.put("message", "No data to show for user "
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

                                if (artists.size() > Constants.MAX_SIZE_5) {
                                    artists.subList(Constants.MAX_SIZE_5, artists.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode artistInfo = objectMapper.createObjectNode();
                                for (Map.Entry<String, Integer> entry : artists) {
                                    artistInfo.put(entry.getKey(), entry.getValue());
                                }
                                result.putPOJO("topArtists", artistInfo);
                            } else {
                                ObjectNode artistInfo = objectMapper.createObjectNode();
                                result.putPOJO("topArtists", artistInfo);
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
                                if (genres.size() > Constants.MAX_SIZE_5) {
                                    genres.subList(Constants.MAX_SIZE_5, genres.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode genreInfo = objectMapper.createObjectNode();
                                for (Map.Entry<String, Integer> entry : genres) {
                                    genreInfo.put(entry.getKey(), entry.getValue());
                                }
                                result.putPOJO("topGenres", genreInfo);
                            } else {
                                ObjectNode genreInfo = objectMapper.createObjectNode();
                                result.putPOJO("topGenres", genreInfo);
                            }

                            //  Sort Song HashMap data and display if available
                            if (!userStats.getTopSongs().isEmpty()) {
                                ArrayList<Map.Entry<String, Integer>> songs;
                                songs = new ArrayList<>(userStats.getTopSongs().entrySet());

                                //  Sort
                                songs.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey()
                                                .compareTo(a2.getKey());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (songs.size() > Constants.MAX_SIZE_5) {
                                    songs.subList(Constants.MAX_SIZE_5, songs.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode songInfo = objectMapper.createObjectNode();
                                for (Map.Entry<String, Integer> entry : songs) {
                                    songInfo.put(entry.getKey(), entry.getValue());
                                }
                                result.putPOJO("topSongs", songInfo);
                            } else {
                                ObjectNode songInfo = objectMapper.createObjectNode();
                                result.putPOJO("topSongs", songInfo);
                            }

                            //  Sort Album HashMap data and display if available
                            if (!userStats.getTopAlbums().isEmpty()) {
                                ArrayList<Map.Entry<String, Integer>> topAlbums;
                                topAlbums = new ArrayList<>(userStats.getTopAlbums().entrySet());

                                //  Sort
                                topAlbums.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().compareTo(a2.getKey());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (topAlbums.size() > Constants.MAX_SIZE_5) {
                                    topAlbums.subList(Constants.MAX_SIZE_5,
                                            topAlbums.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode albumInfo = objectMapper.createObjectNode();
                                for (Map.Entry<String, Integer> entry : topAlbums) {
                                    albumInfo.put(entry.getKey(), entry.getValue());
                                }
                                result.putPOJO("topAlbums", albumInfo);
                            } else {
                                ObjectNode albumInfo = objectMapper.createObjectNode();
                                result.putPOJO("topAlbums", albumInfo);
                            }

                            //  Sort Episode HashMap data and display if available
                            if (!userStats.getTopEpisodes().isEmpty()) {
                                ArrayList<Map.Entry<EpisodeInput, Integer>> episodes;
                                episodes = new ArrayList<>(userStats.getTopEpisodes().entrySet());

                                //  Sort
                                episodes.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getName()
                                                .compareTo(a2.getKey().getName());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (episodes.size() > Constants.MAX_SIZE_5) {
                                    episodes.subList(Constants.MAX_SIZE_5, episodes.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode epInfo = objectMapper.createObjectNode();
                                for (Map.Entry<EpisodeInput, Integer> entry : episodes) {
                                    epInfo.put(entry.getKey().getName(), entry.getValue());
                                }
                                result.putPOJO("topEpisodes", epInfo);
                            } else {
                                ObjectNode epInfo = objectMapper.createObjectNode();
                                result.putPOJO("topEpisodes", epInfo);
                            }
                        }

                    } else if (crtUser.getType().equals("artist")) {
                        //  Get artist stats
                        ArtistStatistics artistStats = null;
                        for (ArtistStatistics artist : Statistics
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

                            wrappedOutput.put("message", "No data to show for artist "
                                    + crtUser.getUsername() + ".");
                        } else {
                            //  Sort Album HashMap data and display if available
                            if (!artistStats.getTopAlbums().isEmpty()) {
                                ArrayList<Map.Entry<String, Integer>> topAlbums;
                                topAlbums = new ArrayList<>(artistStats.getTopAlbums().entrySet());

                                //  Sort
                                topAlbums.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().compareTo(a2.getKey());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (topAlbums.size() > Constants.MAX_SIZE_5) {
                                    topAlbums.subList(Constants.MAX_SIZE_5,
                                            topAlbums.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode albumInfo = objectMapper.createObjectNode();
                                for (Map.Entry<String, Integer> entry : topAlbums) {
                                    albumInfo.put(entry.getKey(), entry.getValue());
                                }
                                result.putPOJO("topAlbums", albumInfo);
                            } else {
                                ObjectNode albumInfo = objectMapper.createObjectNode();
                                result.putPOJO("topAlbums", albumInfo);
                            }

                            //  Sort Song HashMap data and display if available
                            if (!artistStats.getTopSongs().isEmpty()) {
                                ArrayList<Map.Entry<String, Integer>> songs;
                                songs = new ArrayList<>(artistStats.getTopSongs().entrySet());

                                //  Sort
                                songs.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().compareTo(a2.getKey());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (songs.size() > Constants.MAX_SIZE_5) {
                                    songs.subList(Constants.MAX_SIZE_5,
                                            songs.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode songInfo = objectMapper.createObjectNode();
                                for (Map.Entry<String, Integer> entry : songs) {
                                    songInfo.put(entry.getKey(), entry.getValue());
                                }
                                result.putPOJO("topSongs", songInfo);
                            } else {
                                ObjectNode songInfo = objectMapper.createObjectNode();
                                result.putPOJO("topSongs", songInfo);
                            }

                            //  Sort Fans HashMap data and display if available
                            if (!artistStats.getTopFans().isEmpty()) {
                                ArrayList<Map.Entry<UserInput, Integer>> fans;
                                fans = new ArrayList<>(artistStats.getTopFans().entrySet());

                                //  Sort
                                fans.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getUsername()
                                                .compareTo(a2.getKey().getUsername());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (fans.size() > Constants.MAX_SIZE_5) {
                                    fans.subList(Constants.MAX_SIZE_5,
                                            fans.size()).clear();
                                }

                                //  Add info to result array
                                ArrayList<String> fanInfo = new ArrayList<>();
                                for (Map.Entry<UserInput, Integer> entry : fans) {
                                    fanInfo.add(entry.getKey().getUsername());
                                }
                                result.putPOJO("topFans", fanInfo);
                            } else {
                                ObjectNode fanInfo = objectMapper.createObjectNode();
                                result.putPOJO("topFans", fanInfo);
                            }

                            //  Listeners
                            result.putPOJO("listeners", artistStats.getListeners().size());
                        }
                    } else {
                        //  Get host stats
                        HostStatistics hostStats = null;
                        for (HostStatistics host : Statistics
                                .getWrappedStats().getHostsStatistics()) {
                            if (host.getHost().equals(crtUser)) {
                                hostStats = host;
                                break;
                            }
                        }
                        //  See if there is anything to display
                        if (hostStats.getTopEpisodes().isEmpty()
                                && hostStats.getListeners().isEmpty()) {

                            wrappedOutput.put("message", "No data to show for host "
                                    + crtUser.getUsername() + ".");
                        } else {
                            //  Sort Episodes HashMap data and display if available
                            if (!hostStats.getTopEpisodes().isEmpty()) {
                                ArrayList<Map.Entry<EpisodeInput, Integer>> topEpisodes;
                                topEpisodes = new ArrayList<>(hostStats
                                        .getTopEpisodes().entrySet());

                                //  Sort
                                topEpisodes.sort((a1, a2) -> {
                                    if (a2.getValue().equals(a1.getValue())) {
                                        return a1.getKey().getName()
                                                .compareTo(a2.getKey().getName());
                                    }
                                    return a2.getValue().compareTo(a1.getValue());
                                });
                                if (topEpisodes.size() > Constants.MAX_SIZE_5) {
                                    topEpisodes.subList(Constants.MAX_SIZE_5,
                                            topEpisodes.size()).clear();
                                }

                                //  Add info to result array
                                ObjectNode episodeInfo = objectMapper.createObjectNode();
                                for (Map.Entry<EpisodeInput, Integer> entry : topEpisodes) {
                                    episodeInfo.put(entry.getKey().getName(),
                                            entry.getValue());
                                }
                                result.putPOJO("topEpisodes", episodeInfo);
                            } else {
                                ObjectNode albumInfo = objectMapper.createObjectNode();
                                result.putPOJO("topEpisodes", albumInfo);
                            }

                            //  Listeners
                            result.putPOJO("listeners", hostStats.getListeners().size());
                        }
                    }

                    if (!result.isEmpty()) {
                        wrappedOutput.putPOJO("result", result);
                    }
                    outputs.add(wrappedOutput);
                }

                case "buyPremium" -> {
                    ObjectNode buyPremiumOutput = objectMapper.createObjectNode();

                    buyPremiumOutput.put("command", "buyPremium");
                    buyPremiumOutput.put("user", crtCommand.getUsername());
                    buyPremiumOutput.put("timestamp", crtCommand.getTimestamp());

                    String message;

                    //  First check if the user is already premium
                    boolean isPremium = false;
                    for (PremiumUser user : premiumUsers) {
                        if (user.getUser()
                                .equals(crtCommand.getUsername())) {
                            isPremium = true;
                            break;
                        }
                    }
                    //  Then check whether the user has a cancelled subscription
                    PremiumUser cancelledUser = null;
                    for (PremiumUser user : cancelledPremiumUsers) {
                        if (user.getUser()
                                .equals(crtCommand.getUsername())) {
                            cancelledUser = user;
                            break;
                        }
                    }

                    if (isPremium) {
                        message = crtCommand.getUsername() + " is already a premium user.";
                    } else {
                        if (cancelledUser == null) {
                            PremiumUser newUser = new PremiumUser(crtCommand.getUsername());
                            premiumUsers.add(newUser);
                        } else {
                            premiumUsers.add(cancelledUser);
                            cancelledPremiumUsers.remove(cancelledUser);
                        }

                        message = crtCommand.getUsername()
                                + " bought the subscription successfully.";
                    }

                    buyPremiumOutput.put("message", message);
                    outputs.add(buyPremiumOutput);
                }

                case "cancelPremium" -> {
                    ObjectNode cancelPremiumOutput = objectMapper.createObjectNode();

                    cancelPremiumOutput.put("command", "cancelPremium");
                    cancelPremiumOutput.put("user", crtCommand.getUsername());
                    cancelPremiumOutput.put("timestamp", crtCommand.getTimestamp());

                    String message;

                    //  First check if the user is already premium
                    PremiumUser existingUser = null;
                    for (PremiumUser user : premiumUsers) {
                        if (user.getUser()
                                .equals(crtCommand.getUsername())) {
                            existingUser = user;
                            break;
                        }
                    }

                    if (existingUser == null) {
                        message = crtCommand.getUsername() + " is not a premium user.";
                    } else {
                        cancelledPremiumUsers.add(existingUser);
                        premiumUsers.remove(existingUser);
                        message = crtCommand.getUsername()
                                + " cancelled the subscription successfully.";
                    }

                    cancelPremiumOutput.put("message", message);
                    outputs.add(cancelPremiumOutput);
                }

                case "subscribe" -> {
                    ObjectNode subscribeOutput = objectMapper.createObjectNode();

                    subscribeOutput.put("command", "subscribe");
                    subscribeOutput.put("user", crtCommand.getUsername());
                    subscribeOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Find the user's page
                    Page crtPage = null;
                    for (Page page : pageSystem) {
                        if (page.getPageOwner().getUsername()
                                .equals(crtCommand.getUsername())) {
                            crtPage = page;
                            break;
                        }
                    }

                    String message = null;
                    if (crtPage == null) {
                        message = "The username " + crtCommand.getUsername()
                                + " doesn't exist.";
                    } else if (!crtPage.getCurrentPage().equals("ArtistPage")
                            && !crtPage.getCurrentPage().equals("HostPage")) {
                        message = "To subscribe you need to be on the page of"
                                + "an artist or host.";
                    } else {
                        UserInput creator = crtPage.getUserData().getUser();

                        //  Add subscriber to creator channel
                        for (CreatorChannel channel : channels) {
                            if (channel.getCreator().equals(creator)) {
                                //  Find subscriber
                                if (channel.getSubscribers()
                                        .contains(crtCommand.getUsername())) {
                                    channel.getSubscribers().remove(crtCommand.getUsername());
                                    message = crtCommand.getUsername() + " unsubscribed from "
                                            + channel.getCreator().getUsername()
                                            + " successfully.";
                                } else {
                                    channel.getSubscribers().add(crtCommand.getUsername());
                                    message = crtCommand.getUsername() + " subscribed to "
                                            + channel.getCreator().getUsername()
                                            + " successfully.";
                                }
                                break;
                            }
                        }
                    }

                    subscribeOutput.put("message", message);
                    outputs.add(subscribeOutput);
                }

                case "getNotifications" -> {
                    ObjectNode getNotificationsOutput = objectMapper.createObjectNode();

                    getNotificationsOutput.put("command", "getNotifications");
                    getNotificationsOutput.put("user", crtCommand.getUsername());
                    getNotificationsOutput.put("timestamp", crtCommand.getTimestamp());

                    ArrayList<ObjectNode> notifications = new ArrayList<>();

                    //  Find user notifications
                    NotificationBar crtBar = null;
                    for (NotificationBar bar : notificationBars) {
                        if (bar.getSubscriber().equals(crtCommand.getUsername())) {
                            crtBar = bar;
                            break;
                        }
                    }

                    //  Add notifications to display
                    for (String notification : crtBar.getNotifications()) {
                        ObjectNode node = objectMapper.createObjectNode();
                        String[] info = notification.split("/");

                        node.put("name", "New " + info[0]);
                        node.put("description", "New " + info[0] + " from " + info[1] + ".");

                        notifications.add(node);
                    }

                    //  Clear notifications
                    crtBar.getNotifications().clear();

                    getNotificationsOutput.putPOJO("notifications", notifications);
                    outputs.add(getNotificationsOutput);
                }

                case "buyMerch" -> {
                    ObjectNode buyMerchOutput = objectMapper.createObjectNode();

                    buyMerchOutput.put("command", "buyMerch");
                    buyMerchOutput.put("user", crtCommand.getUsername());
                    buyMerchOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Find the user's page
                    Page crtPage = null;
                    for (Page page : pageSystem) {
                        if (page.getPageOwner().getUsername()
                                .equals(crtCommand.getUsername())) {
                            crtPage = page;
                            break;
                        }
                    }

                    String message = null;
                    if (crtPage == null) {
                        message = "The username " + crtCommand.getUsername()
                                + " doesn't exist.";
                    } else if (!crtPage.getCurrentPage().equals("ArtistPage")) {
                        message = "Cannot buy merch from this page.";
                    } else {

                        //  Check for merch
                        Merch wantedMerch = null;
                        for (Management management : managements) {
                            if (management.getArtist().getUsername()
                                    .equals(crtPage.getUserData().getUser().getUsername())) {
                                for (Merch merch : management.getMerches()) {
                                    if (merch.getName().equals(crtCommand.getName())) {
                                        wantedMerch = merch;
                                        break;
                                    }
                                }

                                if (wantedMerch != null) {
                                    break;
                                }
                            }
                        }

                        if (wantedMerch == null) {
                            message = "The merch " + crtCommand.getName()
                                    + " doesn't exist.";
                        } else {
                            //  Add merch to user data
                            for (UserData crtData : usersData) {
                                if (crtData.getUser().getUsername()
                                        .equals(crtCommand.getUsername())) {
                                    //  Add merch to inventory
                                    crtData.getMerches().put(wantedMerch,
                                            crtPage.getUserData().getUser().getUsername());
                                    //  Add merch seller
                                    if (!merchSellers.contains(crtPage.getUserData()
                                            .getUser().getUsername())) {
                                        merchSellers.add(crtPage.getUserData()
                                                .getUser().getUsername());
                                    }

                                    message = crtCommand.getUsername()
                                            + " has added new merch successfully.";
                                    break;
                                }
                            }
                        }
                    }

                    buyMerchOutput.put("message", message);
                    outputs.add(buyMerchOutput);
                }

                case "seeMerch" -> {
                    ObjectNode seeMerchOutput = objectMapper.createObjectNode();

                    seeMerchOutput.put("command", "seeMerch");
                    seeMerchOutput.put("user", crtCommand.getUsername());
                    seeMerchOutput.put("timestamp", crtCommand.getTimestamp());

                    //  Find user data
                    UserData crtData = null;
                    for (UserData userData : usersData) {
                        if (userData.getUser().getUsername()
                                .equals(crtCommand.getUsername())) {
                            crtData = userData;
                            break;
                        }
                    }

                    if (crtData == null) {
                        seeMerchOutput.put("message",
                                "The username " + crtCommand.getUsername() + " doesn't exist.");
                    } else {
                        ArrayList<String> result = new ArrayList<>();
                            for (Map.Entry<Merch, String> merch
                                    : crtData.getMerches().entrySet()) {
                            result.add(merch.getKey().getName());
                        }
                        seeMerchOutput.putPOJO("result", result);
                    }

                    outputs.add(seeMerchOutput);
                }

                default -> {
                }
            }
        }

        //  End of program statistics
        endProgram(objectMapper, merchSellers, premiumUsers, cancelledPremiumUsers,
                usersData, outputs);

        //  Reset Singleton Database
        Statistics.getWrappedStats().resetWrappedStats();

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
                                    final ArrayList<Album> albums,
                                    final ArrayList<PremiumUser> premiumUsers) {
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
                        albums, premiumUsers);

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

    /**
     * This method calculates an artist's revenue on premium users
     *
     * @param premiumUsers Premium Users
     * @param artist Crt artist
     * @param songsAndRevenues All the artist's streamed songs with their revenues
     * @return Song Revenue
     */
    public static double calculateSongRevenue(final ArrayList<PremiumUser> premiumUsers,
                                              final String artist,
                                              final HashMap<String, Double> songsAndRevenues) {
        double songRevenue;
        double totalRevenue = 0;

        for (PremiumUser user : premiumUsers) {
            HashMap<String, Integer> streamedSongs = new HashMap<>();
            int allSongs = user.getPlayedSongs().size();
            int artistSongs = 0;

            for (SongInput song : user.getPlayedSongs()) {
                if (song.getArtist().equals(artist)) {
                    artistSongs++;
                    if (streamedSongs.containsKey(song.getName())) {
                        //  Increase the listen count if the song exists
                        int count = streamedSongs.get(song.getName());
                        streamedSongs.put(song.getName(), count + 1);
                    } else {
                        //  Add the song if it's the first encounter
                        streamedSongs.put(song.getName(), 1);
                    }
                }
            }
            totalRevenue += Constants.SUBSCRIPTION_PRICE / allSongs * artistSongs;
            songRevenue = Constants.SUBSCRIPTION_PRICE / allSongs;

            //  Calculate revenue per song
            for (Map.Entry<String, Integer> songStreams : streamedSongs.entrySet()) {
                double crtRevenue = songRevenue * songStreams.getValue();

                if (songsAndRevenues.containsKey(songStreams.getKey())) {
                    //  Increase the revenue count if the song exists
                    double newRevenue = songsAndRevenues.get(songStreams.getKey()) + crtRevenue;
                    songsAndRevenues.put(songStreams.getKey(), newRevenue);
                } else {
                    //  Add the song if it's the first encounter
                    songsAndRevenues.put(songStreams.getKey(), crtRevenue);
                }
            }
        }

        return totalRevenue;
    }

    /**
     * This method calculates end of program statistics
     *
     * @param objectMapper Object mapper
     * @param merchSellers Merch sellers
     * @param premiumUsers Premium users
     * @param cancelledPremiumUsers Cancelled Premium Users
     * @param usersData Users' Data
     * @param outputs Outputs array
     */
    public static void endProgram(final ObjectMapper objectMapper,
                                  final ArrayList<String> merchSellers,
                                  final ArrayList<PremiumUser> premiumUsers,
                                  final ArrayList<PremiumUser> cancelledPremiumUsers,
                                  final ArrayList<UserData> usersData,
                                  final ArrayNode outputs) {
        //  End of program behaviour
        ObjectNode endProgramOutput = objectMapper.createObjectNode();
        endProgramOutput.put("command", "endProgram");

        ArrayList<String> artistNames = new ArrayList<>();
        ArrayList<ArtistRevenue> artistRevenues = new ArrayList<>();

        //  Collect all artists who may have generated any revenue
        for (ArtistStatistics artist : Statistics.getWrappedStats().getArtistsStatistics()) {
            if (!artist.getTopSongs().isEmpty()
                    || merchSellers.contains(artist.getArtist().getUsername())) {
                artistNames.add(artist.getArtist().getUsername());
            }
        }

        //  For each artist, create a revenue entity
        for (String artist : artistNames) {
            double songRevenue;
            double merchRevenue = 0.0;
            String mostProfitableSong = null;
            HashMap<String, Double> songsAndRevenues = new HashMap<>();

            //  Check for all streamed songs among premium users
            songRevenue = calculateSongRevenue(premiumUsers,
                    artist, songsAndRevenues);
            //  Check for all streamed songs among cancelled premium users
            songRevenue += calculateSongRevenue(cancelledPremiumUsers,
                    artist, songsAndRevenues);


            //  Calculate merch revenue
            for (UserData data : usersData) {
                for (Map.Entry<Merch, String> merch : data.getMerches().entrySet()) {
                    if (merch.getValue().equals(artist)) {
                        merchRevenue += merch.getKey().getPrice();
                    }
                }
            }

            double maxRevenue = 0;
            for (Map.Entry<String, Double> element : songsAndRevenues.entrySet()) {
                if (element.getValue() > maxRevenue) {
                    maxRevenue = element.getValue();
                    mostProfitableSong = element.getKey();
                }
            }

            ArtistRevenue newRevenue;

            if (songRevenue != 0) {
                if (merchRevenue != 0) {
                    newRevenue = new ArtistRevenue.ArtistRevenueBuilder(artist)
                            .setSongRevenue(songRevenue)
                            .setMostProfitableSong(mostProfitableSong)
                            .setMerchRevenue(merchRevenue)
                            .build();
                } else {
                    newRevenue = new ArtistRevenue.ArtistRevenueBuilder(artist)
                            .setSongRevenue(songRevenue)
                            .setMostProfitableSong(mostProfitableSong)
                            .build();
                }
            } else {
                if (merchRevenue != 0) {
                    newRevenue = new ArtistRevenue.ArtistRevenueBuilder(artist)
                            .setMerchRevenue(merchRevenue)
                            .build();
                } else {
                    newRevenue = new ArtistRevenue.ArtistRevenueBuilder(artist)
                            .build();
                }
            }

            artistRevenues.add(newRevenue);
        }

        //  Sort by revenue
        artistRevenues.sort((o1, o2) -> {
            if (o1.getSongRevenue() + o1.getMerchRevenue()
                    == o2.getSongRevenue() + o2.getMerchRevenue()) {
                return o1.getArtist().compareTo(o2.getArtist());
            }
            return Double.compare(o2.getSongRevenue() + o2.getMerchRevenue(),
                    o1.getSongRevenue() + o1.getMerchRevenue());
        });

        //  Display revenue rankings
        ObjectNode result = objectMapper.createObjectNode();
        int ranking = 1;

        for (ArtistRevenue artistRevenue : artistRevenues) {
            ObjectNode node = objectMapper.createObjectNode();
            double roundRevenue;

            roundRevenue = Math.round(artistRevenue.getMerchRevenue()
                    * Constants.LAST_TWO_DIGITS_ADD)
                    / Constants.LAST_TWO_DIGITS_EXTRACT;
            node.put("merchRevenue", roundRevenue);

            roundRevenue = Math.round(artistRevenue.getSongRevenue()
                    * Constants.LAST_TWO_DIGITS_ADD)
                    / Constants.LAST_TWO_DIGITS_EXTRACT;
            node.put("songRevenue", roundRevenue);

            node.put("ranking", ranking);
            node.put("mostProfitableSong", artistRevenue.getMostProfitableSong());

            ranking++;

            result.putPOJO(artistRevenue.getArtist(), node);
        }

        endProgramOutput.putPOJO("result", result);

        outputs.add(endProgramOutput);
    }
}

