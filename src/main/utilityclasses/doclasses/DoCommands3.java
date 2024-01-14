package main.utilityclasses.doclasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import main.commandhelper.Command;
import main.creatorclasses.artistclasses.Merch;
import main.creatorclasses.subscription.NotificationBar;
import main.monetization.PremiumUser;
import main.playlistclasses.Album;
import main.playlistclasses.UserData;
import main.selectionclasses.ItemSelection;
import main.selectionclasses.PodcastSelection;
import main.utilityclasses.Constants;
import main.wrappeddatabase.Statistics;
import main.wrappeddatabase.alluserstats.ArtistStatistics;
import main.wrappeddatabase.alluserstats.HostStatistics;
import main.wrappeddatabase.alluserstats.UserStatistics;

import java.util.ArrayList;
import java.util.Map;

import static main.Main.updatePlayer;

public final class DoCommands3 {

    private DoCommands3() {
    }

    /**
     * This method puts together a crt user's wrap
     *
     * @param objectMapper Object Mapper
     * @param premiumUsers Premium user arraylist
     * @param player App's player
     * @param crtCommand Current command
     * @param podcasts All ongoing podcasts
     * @param library App library
     * @param albums All albums
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doWrapped(final ObjectMapper objectMapper,
                                             final ArrayList<PremiumUser> premiumUsers,
                                             final ArrayList<ItemSelection> player,
                                             final Command crtCommand,
                                             final ArrayList<PodcastSelection> podcasts,
                                             final LibraryInput library,
                                             final ArrayList<Album> albums) {
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

        return wrappedOutput;
    }
    /**
     * Main method call for getNotifications command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command with all its data
     * @param notificationBars The notification bars of all users
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doGetNotifications(final ObjectMapper objectMapper,
                                                final Command crtCommand,
                                                final ArrayList<NotificationBar>
                                                        notificationBars) {
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

        return getNotificationsOutput;
    }

    /**
     * Main method call for getNotifications command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command with all its data
     * @param usersData The data (playlist, merches) of all users
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSeeMerch(final ObjectMapper objectMapper,
                                        final Command crtCommand,
                                        final ArrayList<UserData> usersData) {
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

        return seeMerchOutput;
    }
}
