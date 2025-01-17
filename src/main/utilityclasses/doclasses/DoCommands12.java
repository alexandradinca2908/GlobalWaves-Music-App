package main.utilityclasses.doclasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.UserInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import main.commandhelper.Command;
import main.commandhelper.Filters;
import main.commandhelper.Search;
import main.creatorclasses.artistclasses.Event;
import main.creatorclasses.artistclasses.Management;
import main.creatorclasses.artistclasses.Merch;
import main.creatorclasses.hostclasses.Announcement;
import main.creatorclasses.hostclasses.HostInfo;
import main.likeclasses.ArtistLikes;
import main.likeclasses.SongLikes;
import main.monetization.PremiumUser;
import main.pagingclasses.Page;
import main.playlistclasses.Album;
import main.playlistclasses.Playlist;
import main.playlistclasses.UserData;
import main.selectionclasses.ItemSelection;
import main.selectionclasses.PodcastSelection;
import main.selectionclasses.SongSelection;
import main.selectionclasses.playlists.AlbumSelection;
import main.selectionclasses.playlists.PlaylistSelection;
import main.utilityclasses.Constants;
import main.utilityclasses.getmessageclasses.GetMessages12;
import main.utilityclasses.SearchSelect;
import main.visitorpattern.visitorstring.VisitorString;
import main.visitorpattern.visitorstring.stringclasses.VisitRepeat;
import main.visitorpattern.visitorstring.stringclasses.VisitShuffle;

import java.util.ArrayList;
import java.util.Collections;

import static main.Main.updatePlayer;

public final class DoCommands12 {

    private DoCommands12() {
    }

    /**
     * Main method call for search command
     *
     * @param player           The array that keeps all user players in check
     * @param crtCommand       Current command
     * @param podcasts         The array that keeps track of all the podcasts
     *                         when they are not loaded
     * @param objectMapper     Object Mapper
     * @param library          Singleton containing all songs, users and podcasts
     * @param searches         The array containing all searches
     * @param playlists        The array of all user playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSearch(final ArrayList<ItemSelection> player,
                                      final Command crtCommand,
                                      final ArrayList<PodcastSelection> podcasts,
                                      final ObjectMapper objectMapper,
                                      final LibraryInput library,
                                      final ArrayList<Search> searches,
                                      final ArrayList<Playlist> playlists,
                                      final ArrayList<Album> albums,
                                      final ArrayList<PremiumUser> premiumUsers) {
        //  Searching for a song
        ObjectNode searchOutput = objectMapper.createObjectNode();

        //  Setting the output
        searchOutput.put("command", "search");
        searchOutput.put("user", crtCommand.getUsername());
        searchOutput.put("timestamp", crtCommand.getTimestamp());

        //  Update all players first
        updatePlayer(player, crtCommand, podcasts, library, albums,
                premiumUsers);

        //  Select the user's search
        Search pastSearch = null;
        for (Search search : searches) {
            if (search.getUser().equals(crtCommand.getUsername())) {
                pastSearch = search;
                break;
            }
        }

        //  If there is any leftover search, we must delete it before
        //  allowing the user to search again
        if (pastSearch != null) {
            searches.remove(pastSearch);
        }

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
                        item.updateRemainingTime(crtCommand.getTimestamp(), albums,
                                premiumUsers);
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
                SearchSelect.searchForSongs(filters, result, library,
                        albums);

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
                Search crtSearch = new Search();

                SearchSelect.storeResultForSelect(crtSearch, songNames,
                        "song");
                crtSearch.setSteps(1, 0);
                crtSearch.setUser(crtCommand.getUsername());

                searches.add(crtSearch);
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
                Search crtSearch = new Search();

                SearchSelect.storeResultForSelect(crtSearch, playlistNames,
                        "playlist");
                crtSearch.setSteps(1, 0);
                crtSearch.setUser(crtCommand.getUsername());

                searches.add(crtSearch);
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
                Search crtSearch = new Search();

                SearchSelect.storeResultForSelect(crtSearch, podcastNames,
                        "podcast");
                crtSearch.setSteps(1, 0);
                crtSearch.setUser(crtCommand.getUsername());

                searches.add(crtSearch);
            }

            case "album" -> {
                Filters filters = crtCommand.getFilters();
                ArrayList<Album> result = new ArrayList<>();

                //  Found albums will be added in result array
                SearchSelect.searchForAlbums(filters, result, albums,
                        library);

                //  Truncate results if needed
                if (result.size() > Constants.MAX_SIZE_5) {
                    result.subList(Constants.MAX_SIZE_5, result.size()).clear();
                }

                //  Setting the message
                searchOutput.put("message", "Search returned "
                        + result.size() + " results");

                //  Extracting the names of the albums
                ArrayList<String> albumNames = new ArrayList<>();
                for (Album album : result) {
                    albumNames.add(album.getName());
                }
                searchOutput.putPOJO("results", albumNames);

                //  Storing the result in case we need to select it later
                Search crtSearch = new Search();

                SearchSelect.storeResultForSelect(crtSearch, albumNames,
                        "album");
                crtSearch.setSteps(1, 0);
                crtSearch.setUser(crtCommand.getUsername());

                searches.add(crtSearch);
            }

            case "artist" -> {
                ArrayList<String> artistNames = SearchSelect
                        .setCreatorSearchResults(crtCommand,
                        library, searchOutput);

                //  Storing the result in case we need to select it later
                Search crtSearch = new Search();

                SearchSelect.storeResultForSelect(crtSearch, artistNames,
                        "artist");
                crtSearch.setSteps(1, 0);
                crtSearch.setUser(crtCommand.getUsername());

                searches.add(crtSearch);
            }

            case "host" -> {
                ArrayList<String> hostNames = SearchSelect
                        .setCreatorSearchResults(crtCommand,
                                library, searchOutput);

                //  Storing the result in case we need to select it later
                Search crtSearch = new Search();

                SearchSelect.storeResultForSelect(crtSearch, hostNames,
                        "host");
                crtSearch.setSteps(1, 0);
                crtSearch.setUser(crtCommand.getUsername());

                searches.add(crtSearch);
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
     * @param searches The array containing all searches
     * @param library Singleton containing all songs, users and podcasts
     * @param pageSystem The pages of all user
     * @param usersData Every playlist a user/artist/host owns
     * @param managements All events and merches for every artist
     * @param hostInfos All announcements for every host
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSelect(final ObjectMapper objectMapper,
                                      final Command crtCommand,
                                      final ArrayList<Search> searches,
                                      final LibraryInput library,
                                      final ArrayList<Page> pageSystem,
                                      final ArrayList<UserData> usersData,
                                      final ArrayList<Management> managements,
                                      final ArrayList<HostInfo> hostInfos) {
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

        //  Select the user's search
        Search crtSearch = null;
        for (Search search : searches) {
            if (search.getUser().equals(crtCommand.getUsername())) {
                crtSearch = search;
                break;
            }
        }

        //  Creating the message
        String message = GetMessages12.getSelectMessage(crtSearch,
                crtCommand);
        selectOutput.put("message", message);

        //  Storing the selection in case we need to load it
        if (message.contains("Successfully selected")) {
            int index = crtCommand.getItemNumber();
            String result = crtSearch.getLastSearchResult().get(index);

            //  Keeping only the required value in the array (and its type)
            crtSearch.getLastSearchResult().add(1, result);
            crtSearch.getLastSearchResult()
                    .subList(2, crtSearch.getLastSearchResult().size()).clear();

            //  Last result is initialized properly for loading
            crtSearch.setSteps(1, 1);
        }

        //  If an artist was selected, we change the user's page
        if (message.contains("Successfully selected")
                && (crtSearch.getLastSearchResult().get(0).equals("artist")
                || crtSearch.getLastSearchResult().get(0).equals("host"))) {
            //  Searching for the user's page
            Page crtPage = null;

            for (Page page : pageSystem) {
                if (page.getPageOwner().getUsername()
                        .equals(crtCommand.getUsername())) {
                    crtPage = page;
                    break;
                }
            }

            //  Searching for the creator
            UserInput creator = null;

            for (UserInput user : library.getUsers()) {
                if (user.getUsername().equals(crtSearch.getLastSearchResult().get(1))) {
                    creator = user;
                    break;
                }
            }

            if (creator != null && crtPage != null) {
                //  Add the creator's playlists to the current page
                for (UserData userPlaylists : usersData) {
                    if (userPlaylists.getUser().equals(creator)) {
                        crtPage.setUserData(userPlaylists);
                        break;
                    }
                }

                if (crtSearch.getLastSearchResult().get(0).equals("artist")) {
                    //  Add the artist's management info to the current page
                    for (Management management : managements) {
                        if (management.getArtist().equals(creator)) {
                            crtPage.setManagement(management);
                            break;
                        }
                    }

                    //  Change current page name
                    crtPage.setCurrentPage("ArtistPage");
                } else {
                    //  Add the host's info to the current page
                    for (HostInfo hostInfo : hostInfos) {
                        if (hostInfo.getHost().equals(creator)) {
                            crtPage.setHostInfo(hostInfo);
                            break;
                        }
                    }

                    //  Change current page name
                    crtPage.setCurrentPage("HostPage");
                }

            }

            //  Clearing the result so that we can't load it twice
            searches.remove(crtSearch);
        }

        return selectOutput;
    }

    /**
     * Main method call for load command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param searches The array containing all searches
     * @param library Singleton containing all songs, users and podcasts
     * @param player The array that keeps all user players in check
     * @param playlists The array of all user playlists
     * @param podcasts The array that keeps track of all the podcasts
     *                  when they are not loaded
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doLoad(final ObjectMapper objectMapper,
                                    final Command crtCommand,
                                    final ArrayList<Search> searches,
                                    final LibraryInput library,
                                    final ArrayList<ItemSelection> player,
                                    final ArrayList<Playlist> playlists,
                                    final ArrayList<PodcastSelection> podcasts,
                                    final ArrayList<Album> albums,
                                    final ArrayList<PremiumUser> premiumUsers) {
        ObjectNode loadOutput = objectMapper.createObjectNode();
        loadOutput.put("command", "load");
        loadOutput.put("user", crtCommand.getUsername());
        loadOutput.put("timestamp", crtCommand.getTimestamp());

        updatePlayer(player, crtCommand, podcasts, library, albums,
                premiumUsers);

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

        //  Select the user's search
        Search crtSearch = null;
        for (Search search : searches) {
            if (search.getUser().equals(crtCommand.getUsername())) {
                crtSearch = search;
                break;
            }
        }

        //  Adding the appropriate load message
        if (crtSearch == null || crtSearch.getSteps()[1] == 0) {
            loadOutput.put("message", "Please select a source"
                    + " before attempting to load.");
        } else if (crtSearch.getSteps()[1] == 1 && crtSearch.getLastSearchResult().isEmpty()) {
            loadOutput.put("message", "You can't load an empty audio collection!");
        } else {
            loadOutput.put("message", "Playback loaded successfully.");

            //  Loading the song into the database
            if (crtSearch.getLastSearchResult().get(0).equals("song")) {
                SongSelection selectedSong = SearchSelect.getSongSelection(crtCommand,
                        library, crtSearch.getLastSearchResult());

                //  Clearing other load from the same user
                for (ItemSelection item : player) {
                    if (item.getUser().equals(selectedSong.getUser())) {
                        player.remove(item);
                        break;
                    }
                }

                //  Add selection to array
                player.add(selectedSong);

                //  Record the stats for wrapped
                selectedSong.updateWrappedForSong(selectedSong.getSong(),
                        albums, premiumUsers);
            }

            //  Loading the playlist into the database
            if (crtSearch.getLastSearchResult().get(0).equals("playlist")) {
                PlaylistSelection selectedPlaylist =
                        SearchSelect.getPlaylistSelection(crtCommand,
                                playlists, crtSearch.getLastSearchResult());

                //  Clearing other load from the same user
                for (ItemSelection item : player) {
                    if (item.getUser().equals(selectedPlaylist.getUser())) {
                        player.remove(item);
                        break;
                    }
                }

                //  Add selection to array
                player.add(selectedPlaylist);

                //  Record the stats for wrapped
                //  Only record the first song
                selectedPlaylist.updateWrappedForSong(selectedPlaylist
                        .getPlaylist().getSongs().get(0), albums, premiumUsers);
            }

            //  Loading the podcast into the database
            if (crtSearch.getLastSearchResult().get(0).equals("podcast")) {
                PodcastSelection selectedPodcast =
                        SearchSelect.getPodcastSelection(crtCommand,
                                library, crtSearch.getLastSearchResult());

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

                //  Record the stats for wrapped
                //  We will only record the first episode
                selectedPodcast.updateWrappedForEpisode(selectedPodcast
                        .getPodcast().getEpisodes().get(0));
            }

            //  Loading the album into the database
            if (crtSearch.getLastSearchResult().get(0).equals("album")) {
                AlbumSelection selectedAlbum =
                        SearchSelect.getAlbumSelection(crtCommand,
                                albums, crtSearch.getLastSearchResult());

                //  Clearing other load from the same user
                for (ItemSelection item : player) {
                    if (item.getUser().equals(selectedAlbum.getUser())) {
                        player.remove(item);
                        break;
                    }
                }

                //  Add selection to array
                player.add(selectedAlbum);

                //  Record the stats for wrapped
                //  We will only record the first song
                selectedAlbum.updateWrappedForSong(selectedAlbum
                                .getAlbum().getSongs().get(0), albums, premiumUsers);
            }

            //  Clearing the result so that we can't load it twice
            searches.remove(crtSearch);
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
                                      final LibraryInput library,
                                      final ArrayList<Album> albums,
                                      final ArrayList<PremiumUser> premiumUsers) {
        ObjectNode statusOutput = objectMapper.createObjectNode();
        statusOutput.put("command", "status");
        statusOutput.put("user", crtCommand.getUsername());
        statusOutput.put("timestamp", crtCommand.getTimestamp());

        //  Update the player
        updatePlayer(player, crtCommand, podcasts, library, albums, premiumUsers);

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
                                         final LibraryInput library,
                                         final ArrayList<Album> albums,
                                         final ArrayList<PremiumUser> premiumUsers) {
        ObjectNode playPauseOutput = objectMapper.createObjectNode();

        playPauseOutput.put("command", "playPause");
        playPauseOutput.put("user", crtCommand.getUsername());
        playPauseOutput.put("timestamp", crtCommand.getTimestamp());

        //  Update the player
        updatePlayer(player, crtCommand, podcasts, library, albums, premiumUsers);

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
     * @param usersData The array of users and their respective playlists
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doCreatePlaylist(final ObjectMapper objectMapper,
                                              final Command crtCommand,
                                              final ArrayList<Playlist> playlists,
                                              final ArrayList<UserData> usersData,
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
            for (UserData user : usersData) {
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
     * Main method call for doShowPlaylist command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param usersData The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doShowPlaylists(final ObjectMapper objectMapper,
                                             final Command crtCommand,
                                             final ArrayList<UserData> usersData) {
        ObjectNode showPlaylistsOutput = objectMapper.createObjectNode();

        showPlaylistsOutput.put("command", "showPlaylists");
        showPlaylistsOutput.put("user", crtCommand.getUsername());
        showPlaylistsOutput.put("timestamp", crtCommand.getTimestamp());

        ArrayList<ObjectNode> result = new ArrayList<>();

        UserData user = null;

        for (UserData userP : usersData) {
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

                correctSongOrder = playlist.getSongs();

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
     * @param usersData The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doShowPreferredSongs(final ObjectMapper objectMapper,
                                                  final Command crtCommand,
                                                  final ArrayList<UserData> usersData) {
        ObjectNode showPreferredSongsOutput = objectMapper.createObjectNode();

        showPreferredSongsOutput.put("command", "showPreferredSongs");
        showPreferredSongsOutput.put("user", crtCommand.getUsername());
        showPreferredSongsOutput.put("timestamp", crtCommand.getTimestamp());

        ArrayList<String> songNames = new ArrayList<>();

        for (UserData user : usersData) {
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
                                      final LibraryInput library,
                                      final ArrayList<Album> albums,
                                      final ArrayList<PremiumUser> premiumUsers) {
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
            updatePlayer(player, crtCommand, podcasts, library, albums, premiumUsers);

            //  Now we update repeat status
            VisitorString visitorRepeat = new VisitRepeat();

            message = crtItem.acceptString(visitorRepeat);

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
                                       final LibraryInput library,
                                       final ArrayList<Album> albums,
                                       final ArrayList<PremiumUser> premiumUsers) {
        ObjectNode shuffleOutput = objectMapper.createObjectNode();

        shuffleOutput.put("command", "shuffle");
        shuffleOutput.put("user", crtCommand.getUsername());
        shuffleOutput.put("timestamp", crtCommand.getTimestamp());

        //  Update player first
        updatePlayer(player, crtCommand, podcasts, library, albums, premiumUsers);

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
            VisitorString visitShuffle = new VisitShuffle(crtCommand);
            String message = crtItem.acceptString(visitShuffle);

            shuffleOutput.put("message", message);
        }

        return shuffleOutput;
    }

    /**
     * Main method call for doFollow command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param searches The array containing all searches
     * @param playlists The array of all user playlists
     * @param usersData The array of users and their respective playlists
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doFollow(final ObjectMapper objectMapper,
                                      final Command crtCommand,
                                      final ArrayList<Search> searches,
                                      final ArrayList<Playlist> playlists,
                                      final ArrayList<UserData> usersData,
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

        //  Select the user's search
        Search crtSearch = null;
        for (Search search : searches) {
            if (search.getUser().equals(crtCommand.getUsername())) {
                crtSearch = search;
                break;
            }
        }

        //  Adding the appropriate load message
        //  No select or no results found
        if (crtSearch == null || crtSearch.getSteps()[1] == 0) {
            followOutput.put("message", "Please select a source "
                    + "before following or unfollowing.");

            //  Source is not a playlist
        } else if (!crtSearch.getLastSearchResult().get(0).equals("playlist")) {
            followOutput.put("message", "The selected source is not a playlist.");

        } else {
            //  Localize the specific playlist
            Playlist wantedPlaylist = null;
            for (Playlist playlist : playlists) {
                if (playlist.getName().equals(crtSearch.getLastSearchResult().get(1))) {
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
                String message = GetMessages12.getFollowMessage(wantedPlaylist,
                        crtCommand, usersData);
                followOutput.put("message", message);
            }
        }

        return followOutput;
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
     * Main method call for doShowAlbums command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param usersData The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doShowAlbums(final ObjectMapper objectMapper,
                                          final Command crtCommand,
                                          final ArrayList<UserData> usersData) {
        ObjectNode showAlbumsOutput = objectMapper.createObjectNode();

        showAlbumsOutput.put("command", "showAlbums");
        showAlbumsOutput.put("user", crtCommand.getUsername());
        showAlbumsOutput.put("timestamp", crtCommand.getTimestamp());

        UserData crtUser = null;

        //  Search for the user's playlists
        for (UserData userPlaylists : usersData) {
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

    /**
     * Main method call for doPrintCurrentPage command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param pageSystem Array of all the pages in the system
     * @param library Singleton containing all songs, users and podcasts
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doPrintCurrentPage(final ObjectMapper objectMapper,
                                                final Command crtCommand,
                                                final ArrayList<Page> pageSystem,
                                                final LibraryInput library,
                                                final ArrayList<SongLikes> songsLikes) {

        ObjectNode printCurrentPageOutput = objectMapper.createObjectNode();

        printCurrentPageOutput.put("user", crtCommand.getUsername());
        printCurrentPageOutput.put("command", "printCurrentPage");
        printCurrentPageOutput.put("timestamp", crtCommand.getTimestamp());

        //  Check online status
        //  If user is offline, we exit the function before any action can be done
        for (UserInput user : library.getUsers()) {
            if (user.getUsername().equals(crtCommand.getUsername())) {
                if (!user.isOnline()) {
                    printCurrentPageOutput.put("message", user.getUsername() + " is offline.");

                    return printCurrentPageOutput;
                }
            }
        }

        //  Searching for the user
        Page crtPage = null;

        for (Page page : pageSystem) {
            if (page.getPageOwner().getUsername()
                    .equals(crtCommand.getUsername())) {
                crtPage = page;
                break;
            }
        }

        //  Depending on what page the user is, we print the appropriate message
        String message = null;

        if (crtPage != null) {
            switch (crtPage.getCurrentPage()) {
                case "Home" -> {
                    ArrayList<String> likedSongs = new ArrayList<>();
                    ArrayList<SongInput> songs = crtPage.getUserData().getLikedSongs();
                    ArrayList<SongLikes> sortedSongs = new ArrayList<>();

                    //  Get first 5 liked songs
                    for (SongInput song : songs) {
                        for (SongLikes songLikes : songsLikes) {
                            if (song.equals(songLikes.getSong())) {
                                sortedSongs.add(songLikes);
                                break;
                            }
                        }
                    }

                    sortedSongs.sort((s1, s2) -> s2.getLikes() - s1.getLikes());
                    if (sortedSongs.size() > Constants.MAX_SIZE_5) {
                        sortedSongs.subList(Constants.MAX_SIZE_5, sortedSongs.size()).clear();
                    }

                    //  Add their names in the result array
                    for (SongLikes song : sortedSongs) {
                        likedSongs.add(song.getSong().getName());
                    }

                    //  Get first 5 followed playlists
                    ArrayList<String> followedPlaylists = new ArrayList<>();
                    ArrayList<Playlist> playlistsArray =
                            crtPage.getUserData().getFollowedPlaylists();

                    playlistsArray.sort((p1, p2) ->
                            p2.getFollowers().size() - p1.getFollowers().size());
                    if (playlistsArray.size() > Constants.MAX_SIZE_5) {
                        playlistsArray.subList(Constants.MAX_SIZE_5, playlistsArray.size()).clear();
                    }

                    //  Add their names in the result array
                    for (Playlist playlist : playlistsArray) {
                        followedPlaylists.add(playlist.getName());
                    }

                    //  Get message
                    message = "Liked songs:\n\t" + likedSongs
                            + "\n\nFollowed playlists:\n\t"
                            + followedPlaylists;
                }

                case "LikedContent" -> {
                    //  Get liked songs
                    ArrayList<String> likedSongs = new ArrayList<>();
                    ArrayList<SongInput> songs =
                            crtPage.getUserData().getLikedSongs();

                    for (SongInput song : songs) {
                        likedSongs.add(song.getName()
                                + " - " + song.getArtist());
                    }

                    //  Get followed playlists
                    ArrayList<String> followedPlaylists = new ArrayList<>();
                    ArrayList<Playlist> playlistsArray =
                            crtPage.getUserData().getFollowedPlaylists();

                    for (Playlist playlist : playlistsArray) {
                        followedPlaylists.add(playlist.getName()
                                + " - " + playlist.getOwner());
                    }

                    //  Get message
                    message = "Liked songs:\n\t" + likedSongs
                            + "\n\nFollowed playlists:\n\t"
                            + followedPlaylists;
                }

                case "ArtistPage" -> {
                    //  Get albums
                    ArrayList<String> artistAlbums = new ArrayList<>();
                    ArrayList<Album> albumsArray =
                            crtPage.getUserData().getAlbums();

                    for (Album album : albumsArray) {
                        artistAlbums.add(album.getName());
                    }

                    //  Get merch
                    if (crtPage.getManagement() != null) {
                        ArrayList<String> artistMerch = new ArrayList<>();
                        ArrayList<Merch> merchArray =
                                crtPage.getManagement().getMerches();

                        for (Merch merch : merchArray) {
                            artistMerch.add(merch.getName()
                                    + " - " + merch.getPrice()
                                    + ":\n\t"
                                    + merch.getDescription());
                        }

                        //  Get events
                        ArrayList<String> artistEvents = new ArrayList<>();
                        ArrayList<Event> eventArray =
                                crtPage.getManagement().getEvents();

                        for (Event event : eventArray) {
                            artistEvents.add(event.getName()
                                    + " - " + event.getDate()
                                    + ":\n\t"
                                    + event.getDescription());
                        }

                        //  Get message
                        message = "Albums:\n\t" + artistAlbums
                                + "\n\nMerch:\n\t"
                                + artistMerch + "\n\nEvents:\n\t"
                                + artistEvents;
                    }
                }

                case "HostPage" -> {
                    //  Get podcasts
                    ArrayList<String> hostPodcasts = new ArrayList<>();
                    ArrayList<PodcastInput> podcastArray =
                            crtPage.getUserData().getPodcasts();

                    for (PodcastInput podcast : podcastArray) {
                        String podcastString = podcast.getName() + ":\n\t[";
                        for (EpisodeInput episode : podcast.getEpisodes()) {
                            podcastString += episode.getName() + " - "
                                    + episode.getDescription() + ", ";
                        }
                        String finalString = podcastString.substring(0, podcastString.length() - 2)
                                + "]\n";
                        hostPodcasts.add(finalString);
                    }

                    //  Get merch
                    if (crtPage.getHostInfo() != null) {
                        ArrayList<String> hostAnnouncements = new ArrayList<>();
                        ArrayList<Announcement> announcementArray =
                                crtPage.getHostInfo().getAnnouncements();

                        for (Announcement announcement : announcementArray) {
                            hostAnnouncements.add(announcement.getName()
                                    + ":\n\t" + announcement.getDescription()
                                    + "\n");
                        }

                        //  Get message
                        message = "Podcasts:\n\t" + hostPodcasts
                                + "\n\nAnnouncements:\n\t"
                                + hostAnnouncements;
                    }
                }

                default -> { }

            }
        }

        printCurrentPageOutput.put("message", message);

        return printCurrentPageOutput;
    }

    /**
     * Main method call for doGetAllUsers command
     *
     * @param objectMapper Object Mapper
     * @param library Singleton containing all songs, users and podcasts
     * @param crtCommand Current command
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doGetAllUsers(final ObjectMapper objectMapper,
                                           final LibraryInput library,
                                           final Command crtCommand) {
        ObjectNode getAllUsersOutput = objectMapper.createObjectNode();

        getAllUsersOutput.put("command", "getAllUsers");
        getAllUsersOutput.put("timestamp", crtCommand.getTimestamp());

        ArrayList<String> result = new ArrayList<>();

        //  Parsing user list 3 times

        //  Normal users
        for (UserInput user : library.getUsers()) {
            if (user.getType().equals("user")) {
                result.add(user.getUsername());
            }
        }
        //  Artists
        for (UserInput user : library.getUsers()) {
            if (user.getType().equals("artist")) {
                result.add(user.getUsername());
            }
        }
        //  Hosts
        for (UserInput user : library.getUsers()) {
            if (user.getType().equals("host")) {
                result.add(user.getUsername());
            }
        }

        getAllUsersOutput.putPOJO("result", result);

        return getAllUsersOutput;
    }


    /**
     * Main method call for doShowPodcasts command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param usersData The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doShowPodcasts(final ObjectMapper objectMapper,
                                            final Command crtCommand,
                                            final ArrayList<UserData> usersData) {
        ObjectNode showPodcastsOutput = objectMapper.createObjectNode();

        showPodcastsOutput.put("command", "showPodcasts");
        showPodcastsOutput.put("user", crtCommand.getUsername());
        showPodcastsOutput.put("timestamp", crtCommand.getTimestamp());

        UserData crtUser = null;

        //  Search for the user's playlists
        for (UserData userPlaylists : usersData) {
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

        return showPodcastsOutput;
    }

    /**
     * Main method call for doGetTop5Albums command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param albums The array of all albums in the database
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doGetTop5Albums(final ObjectMapper objectMapper,
                                             final Command crtCommand,
                                             final ArrayList<Album> albums) {
        ObjectNode topAlbumsOutput = objectMapper.createObjectNode();

        topAlbumsOutput.put("command", "getTop5Albums");
        topAlbumsOutput.put("timestamp", crtCommand.getTimestamp());

        //  Sort the songs in a separate array and then take the first 5 results
        ArrayList<Album> sortedAlbumsByLikes = new ArrayList<>(albums);
        sortedAlbumsByLikes.sort((album1, album2) -> {
            if (album2.calculateAlbumLikes()
                    == album1.calculateAlbumLikes()) {
                return album1.getName().compareTo(album2.getName());
            }

            return album2.calculateAlbumLikes()
                    - album1.calculateAlbumLikes();
        });

        //  Truncate the result to 5
        if (sortedAlbumsByLikes.size() > Constants.MAX_SIZE_5) {
            sortedAlbumsByLikes.subList(Constants.MAX_SIZE_5,
                    sortedAlbumsByLikes.size()).clear();
        }

        //  Store names
        ArrayList<String> result = new ArrayList<>();
        for (Album album : sortedAlbumsByLikes) {
            result.add(album.getName());
        }

        topAlbumsOutput.putPOJO("result", result);

        return topAlbumsOutput;
    }


    /**
     * Main method call for doGetTop5Artists command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command
     * @param usersData The array of users and their respective playlists
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doGetTop5Artists(final ObjectMapper objectMapper,
                                              final Command crtCommand,
                                              final ArrayList<UserData> usersData) {
        ObjectNode topAlbumsOutput = objectMapper.createObjectNode();

        topAlbumsOutput.put("command", "getTop5Artists");
        topAlbumsOutput.put("timestamp", crtCommand.getTimestamp());

        //  Sort the songs in a separate array and then take the first 5 results
        ArrayList<ArtistLikes> sortedArtists = new ArrayList<>();

        for (UserData user : usersData) {
            if (user.getUser().getType().equals("artist")) {
                ArtistLikes newArtist = new ArtistLikes();

                //  Set user
                newArtist.setUser(user.getUser());
                //  Set likes
                int likes = 0;
                for (Album album : user.getAlbums()) {
                    likes += album.calculateAlbumLikes();
                }
                newArtist.setLikes(likes);

                sortedArtists.add(newArtist);
            }
        }

        sortedArtists.sort((a1, a2) -> a2.getLikes() - a1.getLikes());

        //  Truncate the result to 5
        if (sortedArtists.size() > Constants.MAX_SIZE_5) {
            sortedArtists.subList(Constants.MAX_SIZE_5,
                    sortedArtists.size()).clear();
        }

        //  Store names
        ArrayList<String> result = new ArrayList<>();
        for (ArtistLikes artist : sortedArtists) {
            result.add(artist.getUser().getUsername());
        }

        topAlbumsOutput.putPOJO("result", result);

        return topAlbumsOutput;
    }
}
